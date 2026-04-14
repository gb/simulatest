package org.simulatest.environment;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Supplier;

import org.simulatest.environment.infra.ExceptionAggregator;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Encapsulates the lifecycle of a Simulatest test run: plugin management,
 * factory resolution, and Insistence Layer discovery.
 *
 * <p>Both the JUnit 4 and JUnit 5 integrations open a session at the start
 * of a suite run and close it when the suite completes. This keeps the
 * orchestration logic in one place instead of duplicated across runners.</p>
 */
public final class SimulatestSession implements AutoCloseable {

	private final List<SimulatestPlugin> plugins;
	private final EnvironmentFactory factory;
	private final InsistenceLayer insistenceLayer;
	private final AtomicBoolean closed = new AtomicBoolean(false);

	private SimulatestSession(List<SimulatestPlugin> plugins, EnvironmentFactory factory,
							  InsistenceLayer insistenceLayer) {
		this.plugins = plugins;
		this.factory = factory;
		this.insistenceLayer = insistenceLayer;
	}

	public static List<SimulatestPlugin> loadPlugins() {
		return ServiceLoader.load(SimulatestPlugin.class).stream()
				.map(ServiceLoader.Provider::get)
				.toList();
	}

	public static EnvironmentFactory resolveFactory(List<SimulatestPlugin> plugins) {
		return plugins.stream()
				.map(SimulatestPlugin::environmentFactory)
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(EnvironmentReflectionFactory::new);
	}

	public static void initializePlugins(List<SimulatestPlugin> plugins, Collection<Class<?>> testClasses) {
		for (SimulatestPlugin plugin : plugins) plugin.initialize(testClasses);
	}

	public static void destroyPlugins(List<SimulatestPlugin> plugins) {
		ExceptionAggregator failures = new ExceptionAggregator();
		for (SimulatestPlugin plugin : plugins) failures.capture(plugin::destroy);
		failures.throwIfAny();
	}

	public static Object createTestInstanceOrElse(List<SimulatestPlugin> plugins, Class<?> testClass,
												   Supplier<Object> fallback) {
		return plugins.stream()
				.map(plugin -> plugin.createTestInstance(testClass))
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(fallback);
	}

	public static void postProcessWithPlugins(List<SimulatestPlugin> plugins, Object instance) {
		for (SimulatestPlugin plugin : plugins)
			plugin.postProcessTestInstance(instance);
	}

	/**
	 * Loads plugins via ServiceLoader, initializes them, resolves the factory
	 * and Insistence Layer. Typical entry point for JUnit 5.
	 */
	public static SimulatestSession open(Collection<Class<?>> testClasses) {
		List<SimulatestPlugin> plugins = loadPlugins();
		return open(plugins, testClasses);
	}

	/**
	 * Initializes pre-loaded plugins, resolves the factory and Insistence Layer.
	 * Used by JUnit 4 where plugins are loaded early (in the constructor) but
	 * initialized late (in {@code run()}).
	 */
	public static SimulatestSession open(List<SimulatestPlugin> plugins,
										 Collection<Class<?>> testClasses) {
		Objects.requireNonNull(plugins, "plugins must not be null");
		initializePlugins(plugins, testClasses);
		return new SimulatestSession(
				List.copyOf(plugins),
				resolveFactory(plugins),
				InsistenceLayerFactory.resolve().orElse(null));
	}

	public List<SimulatestPlugin> plugins() {
		return Collections.unmodifiableList(plugins);
	}

	public EnvironmentFactory factory() {
		return factory;
	}

	public Optional<InsistenceLayer> insistenceLayer() {
		return Optional.ofNullable(insistenceLayer);
	}

	/**
	 * Asks each plugin in order to create a test instance, falling back to
	 * {@code fallback} when no plugin produces one.
	 */
	public Object createTestInstance(Class<?> testClass, Supplier<Object> fallback) {
		return createTestInstanceOrElse(plugins, testClass, fallback);
	}

	/**
	 * Notifies every plugin to post-process a test instance (e.g., inject dependencies).
	 */
	public void postProcessTestInstance(Object instance) {
		postProcessWithPlugins(plugins, instance);
	}

	/**
	 * Runs {@code action} inside an outer Insistence Layer bracket if a layer
	 * is configured; otherwise just runs the action. Aggregates cleanup errors
	 * as suppressed on the original exception.
	 */
	public void run(Runnable action) {
		if (insistenceLayer != null) insistenceLayer.runIsolated(action);
		else action.run();
	}

	/**
	 * Resets the current Insistence Layer level — the standard between-tests
	 * hook so sibling tests start from the same database state.
	 * No-op when no Insistence Layer is configured.
	 */
	public void afterTest() {
		if (insistenceLayer != null) insistenceLayer.resetCurrentLevel();
	}

	@Override
	public void close() {
		if (!closed.compareAndSet(false, true)) return;
		destroyPlugins(plugins);
	}

}
