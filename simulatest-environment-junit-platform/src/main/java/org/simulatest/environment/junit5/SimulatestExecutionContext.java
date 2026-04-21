package org.simulatest.environment.junit5;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.plugin.EagerEnvironmentLifecycle;
import org.simulatest.environment.plugin.EnvironmentExecution;
import org.simulatest.environment.plugin.EnvironmentLifecycle;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * Per-engine execution context bridging a {@link SimulatestSession} into
 * JUnit Platform's {@link EngineExecutionContext}. Implements
 * {@link EnvironmentExecution} so {@link EnvironmentLifecycle}
 * implementations receive a narrow callback surface instead of the full
 * engine state.
 *
 * <p><b>Thread-safety:</b> the instance itself is not thread-safe — Jupiter
 * invokes lifecycle methods on a single executor thread per engine. The
 * {@link #getCurrent() current-context} ThreadLocal is per-thread by design
 * and returns empty if an extension runs on a thread that never set it.</p>
 */
public final class SimulatestExecutionContext implements EngineExecutionContext, EnvironmentExecution {

	private static final EnvironmentLifecycle DEFAULT_LIFECYCLE = new EagerEnvironmentLifecycle();

	static final SimulatestExecutionContext EMPTY = new SimulatestExecutionContext(null, null, null, List.of());

	// Bridges the Simulatest engine context into Jupiter's internal execution so
	// that auto-detected extensions (InsistenceAfterEachExtension, etc.) can access it.
	// NOTE: if Jupiter's parallel execution moves @AfterEach hooks to a different
	// thread than the one that set the context, getCurrent() returns empty.
	private static final ThreadLocal<SimulatestExecutionContext> CURRENT = new ThreadLocal<>();

	private final SimulatestSession session;
	private final EnvironmentFactory factory;
	private final InsistenceLayer insistenceLayer;
	private final List<SimulatestPlugin> plugins;
	private final EnvironmentLifecycle lifecycle;

	public SimulatestExecutionContext(SimulatestSession session) {
		this(
				session,
				session != null ? session.factory() : null,
				session != null ? session.insistenceLayer().orElse(null) : null,
				session != null ? session.plugins() : List.of());
	}

	public SimulatestExecutionContext(SimulatestSession session, EnvironmentFactory factory,
			InsistenceLayer insistenceLayer, List<SimulatestPlugin> plugins) {
		this.session = session;
		this.factory = factory;
		this.insistenceLayer = insistenceLayer;
		this.plugins = plugins;
		this.lifecycle = selectLifecycle(plugins);
	}

	private static EnvironmentLifecycle selectLifecycle(List<SimulatestPlugin> plugins) {
		return plugins.stream()
				.map(SimulatestPlugin::environmentLifecycle)
				.filter(Objects::nonNull)
				.findFirst()
				.orElse(DEFAULT_LIFECYCLE);
	}

	public Optional<InsistenceLayer> insistenceLayer() {
		return Optional.ofNullable(insistenceLayer);
	}

	public Optional<EnvironmentFactory> factory() {
		return Optional.ofNullable(factory);
	}

	public List<SimulatestPlugin> plugins() {
		return plugins;
	}

	/** The lifecycle chosen for this session. Descriptors delegate enter/exit to it. */
	public EnvironmentLifecycle lifecycle() {
		return lifecycle;
	}

	@Override
	public void runEnvironment(EnvironmentDefinition definition) {
		Objects.requireNonNull(definition, "definition must not be null");
		EnvironmentFactory envFactory = factory().orElseThrow(() -> new IllegalStateException(
				"Cannot run environment '" + definition.getName() + "': no Simulatest session open"));
		try {
			envFactory.create(definition).run();
		} catch (Exception exception) {
			throw new EnvironmentExecutionException(
					"Failed during run for environment '" + definition.getName() + "'", exception);
		}
	}

	@Override
	public void increaseInsistenceLevel() {
		ifInsistenceLayer(InsistenceLayer::increaseLevel);
	}

	@Override
	public void decreaseInsistenceLevel() {
		ifInsistenceLayer(InsistenceLayer::decreaseLevelOrCleanup);
	}

	public void resetInsistenceLevel() {
		ifInsistenceLayer(InsistenceLayer::resetCurrentLevel);
	}

	public void postProcessTestInstance(Object instance) {
		if (session != null) session.postProcessTestInstance(instance);
	}

	private void ifInsistenceLayer(Consumer<InsistenceLayer> action) {
		insistenceLayer().ifPresent(action);
	}

	public void close() {
		if (session != null) session.close();
	}

	public static SimulatestExecutionContext getCurrent() {
		return CURRENT.get();
	}

	/**
	 * Binds {@code context} to the current thread for the duration of
	 * {@code action}, clearing the binding on return even if the action throws.
	 * Prevents callers from forgetting to clear the ThreadLocal.
	 */
	public static void withCurrent(SimulatestExecutionContext context, Runnable action) {
		CURRENT.set(context);
		try {
			action.run();
		} finally {
			CURRENT.remove();
		}
	}

}
