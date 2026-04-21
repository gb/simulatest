package org.simulatest.di.quarkus;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.simulatest.environment.Environment;
import org.simulatest.environment.junit5.DeferredEnvironmentCoordinator;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Jupiter extension that runs the deferred environments for a test class
 * after Quarkus's Arc container has booted.
 *
 * <p>Registered for auto-detection via
 * {@code META-INF/services/org.junit.jupiter.api.extension.Extension}; the
 * inner Jupiter session that Simulatest launches for each test class has
 * auto-detection enabled, so this fires without the user adding
 * {@code @ExtendWith}.
 *
 * <p>Ordering: {@code QuarkusTestExtension#beforeAll} boots Arc. This
 * extension's {@code beforeAll} runs afterwards, resolves the test class's
 * environment ancestry from the {@code @UseEnvironment} chain, and for each
 * ancestor not yet run in this suite instantiates it through Arc (so its
 * {@code @Inject} fields are populated), invokes {@code run()}, and pushes
 * an Insistence Layer level. A sibling test class entering later sees the
 * already-claimed ancestors and only runs its own leaf environment.
 */
public final class QuarkusEnvironmentJupiterExtension implements BeforeAllCallback {

	@Override
	public void beforeAll(ExtensionContext context) {
		Class<?> testClass = context.getRequiredTestClass();
		List<Class<? extends Environment>> ancestry = DeferredEnvironmentCoordinator.ancestryOf(testClass);
		if (ancestry.isEmpty()) return;

		InsistenceLayer layer = InsistenceLayerFactory.resolve().orElseThrow(() -> new IllegalStateException(
				"Insistence Layer is not configured. Ensure SimulatestQuarkusPlugin has initialized "
				+ "before the test class enters Quarkus's lifecycle."));

		for (Class<? extends Environment> environmentClass : ancestry) {
			if (!DeferredEnvironmentCoordinator.claimNotYetRun(environmentClass)) continue;
			runEnvironment(environmentClass);
			layer.increaseLevel();
		}
	}

	private static void runEnvironment(Class<? extends Environment> environmentClass) {
		Environment environment = resolveFromArc(environmentClass);
		if (environment == null) environment = reflectivelyInstantiate(environmentClass);
		environment.run();
	}

	// Arc returns null when the class isn't a discovered bean. That's a valid
	// case for environments the user chose to leave un-annotated — we fall back
	// to reflection, matching the behavior the other DI plugins' environment
	// factories already expose.
	private static Environment resolveFromArc(Class<? extends Environment> environmentClass) {
		ArcContainer container = Arc.container();
		if (container == null) return null;
		InstanceHandle<? extends Environment> handle = container.instance(environmentClass);
		return handle.isAvailable() ? handle.get() : null;
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
