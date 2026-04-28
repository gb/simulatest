package org.simulatest.di.quarkus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.simulatest.environment.Environment;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Jupiter extension that runs the Simulatest environment ancestry for a
 * test class after Quarkus's Arc container has booted.
 *
 * <p>Auto-discovered via
 * {@code META-INF/services/org.junit.jupiter.api.extension.Extension}; the
 * inner Jupiter session that {@code @QuarkusTest} runs has auto-detection
 * enabled, so this fires without the user adding {@code @ExtendWith}.
 *
 * <p>Ordering: {@code QuarkusTestExtension#beforeAll} boots Arc. This
 * extension's {@code beforeAll} runs afterwards, resolves the test class's
 * environment ancestry from the {@code @UseEnvironment} chain, and for each
 * ancestor not yet seen in this suite instantiates it through Arc (so
 * {@code @Inject} fields populate), invokes {@code run()}, and pushes an
 * Insistence Layer level. A sibling test class entering later sees the
 * already-claimed ancestors and only runs its own leaf environment.
 *
 * <p>When a new Arc container is observed (typically a {@code @TestProfile}
 * switch), savepoints pushed under the old container are unwound and the
 * coordinator is reset, so the new runtime starts from a clean stack.
 */
public final class PostArcEnvironmentRunner implements BeforeAllCallback, BeforeEachCallback, AfterEachCallback {

	private static final Logger logger = LoggerFactory.getLogger(PostArcEnvironmentRunner.class);

	// Different instance on the next beforeAll means Quarkus restarted
	// (e.g. @TestProfile switch); we reset state in that case.
	private static ArcContainer lastKnownContainer;

	@Override
	public void beforeAll(ExtensionContext context) {
		unwindStateIfQuarkusRestarted();

		Class<?> testClass = context.getRequiredTestClass();
		List<Class<? extends Environment>> ancestry = DeferredEnvironmentCoordinator.ancestryOf(testClass);
		if (ancestry.isEmpty()) return;

		InsistenceLayer layer = InsistenceLayerFactory.resolve().orElseThrow(() -> new IllegalStateException(
				"Insistence Layer is not configured. Ensure SimulatestQuarkusTestResource has run "
				+ "before the test class enters Quarkus's lifecycle."));

		for (Class<? extends Environment> environmentClass : ancestry) {
			if (!DeferredEnvironmentCoordinator.claimNotYetRun(environmentClass)) continue;
			runAndPushOrRollbackClaim(environmentClass, layer);
		}
	}

	/**
	 * Pushes a per-test Insistence Layer level so anything the test method
	 * writes can be rolled back independently of fixture state. Runs after
	 * Quarkus's transactional interceptors but before user code, since the
	 * extension is auto-discovered and Quarkus's own interceptors are at
	 * higher priority.
	 */
	@Override
	public void beforeEach(ExtensionContext context) {
		InsistenceLayerFactory.resolve().ifPresent(InsistenceLayer::increaseLevel);
	}

	/**
	 * Rolls back everything the test method wrote. Pops the per-test level
	 * pushed in {@link #beforeEach(ExtensionContext)}. Outer levels (e.g.
	 * environment fixture state) are preserved so the next test starts from
	 * the same baseline.
	 */
	@Override
	public void afterEach(ExtensionContext context) {
		InsistenceLayerFactory.resolve().ifPresent(InsistenceLayer::decreaseLevel);
	}

	// Detects a new Arc container and unwinds both the logical (coordinator)
	// and physical (savepoint stack) state belonging to the previous one.
	// Without the physical unwind, levels pushed under the old Arc would
	// remain on the connection; the coordinator would then claim ancestors
	// freshly and push again on top, corrupting rollback boundaries.
	private static synchronized void unwindStateIfQuarkusRestarted() {
		ArcContainer current = currentArcContainer();
		if (current == null || current == lastKnownContainer) return;
		if (lastKnownContainer != null) {
			logger.info("Arc container changed identity (likely @TestProfile switch); unwinding deferred-environment state");
			InsistenceLayerFactory.resolve().ifPresent(layer -> layer.setLevelTo(0));
			DeferredEnvironmentCoordinator.reset();
		}
		lastKnownContainer = current;
	}

	private static ArcContainer currentArcContainer() {
		try {
			return Arc.container();
		} catch (RuntimeException absent) {
			return null;
		}
	}

	// If the env's run() or the savepoint push fails, release the claim so
	// subsequent test classes can retry rather than silently skip on a stale
	// record. The push record is only set AFTER increaseLevel returns, so the
	// matching onExit skips popping when a failure prevented the push.
	private static void runAndPushOrRollbackClaim(Class<? extends Environment> environmentClass, InsistenceLayer layer) {
		try {
			runEnvironment(environmentClass);
			layer.increaseLevel();
			DeferredEnvironmentCoordinator.recordPush(environmentClass);
		} catch (Throwable propagate) {
			DeferredEnvironmentCoordinator.forget(environmentClass);
			throw propagate;
		}
	}

	// Threads the Arc InstanceHandle through so @Dependent-scoped environments
	// are properly destroyed after run() completes. For @ApplicationScoped
	// and @Singleton beans close() is a no-op; for @Dependent it triggers
	// the disposal chain that would otherwise leak the instance.
	private static void runEnvironment(Class<? extends Environment> environmentClass) {
		InstanceHandle<? extends Environment> arcHandle = resolveFromArc(environmentClass);
		try {
			Environment environment = arcHandle != null
					? arcHandle.get()
					: warnThenReflect(environmentClass);
			environment.run();
		} finally {
			if (arcHandle != null) arcHandle.close();
		}
	}

	private static Environment warnThenReflect(Class<? extends Environment> environmentClass) {
		logger.warn(
			"{} is not a discovered Arc bean; falling back to reflection. Add @Dependent or "
			+ "@ApplicationScoped if the environment uses @Inject fields, otherwise they will be null.",
			environmentClass.getName());
		return reflectivelyInstantiate(environmentClass);
	}

	private static InstanceHandle<? extends Environment> resolveFromArc(Class<? extends Environment> environmentClass) {
		ArcContainer container = currentArcContainer();
		if (container == null) return null;

		InstanceHandle<? extends Environment> handle = container.instance(environmentClass);
		if (!handle.isAvailable()) {
			handle.close();
			return null;
		}
		return handle;
	}

	private static Environment reflectivelyInstantiate(Class<? extends Environment> environmentClass) {
		try {
			var constructor = environmentClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(
					"Failed to instantiate environment " + environmentClass.getName(),
					e.getCause() != null ? e.getCause() : e);
		} catch (ReflectiveOperationException e) {
			throw new IllegalStateException(
					"Failed to instantiate environment " + environmentClass.getName(), e);
		}
	}

}
