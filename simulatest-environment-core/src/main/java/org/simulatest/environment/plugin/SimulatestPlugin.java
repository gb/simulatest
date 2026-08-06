package org.simulatest.environment.plugin;

import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.SimulatestSession;

import java.util.Collection;
import java.util.Optional;

/**
 * Service Provider Interface for integrating dependency injection frameworks
 * (Spring, CDI, Guice, etc.) with Simulatest.
 *
 * <p>Implementations are discovered via {@link java.util.ServiceLoader}.
 * A single plugin bridges a DI framework into both JUnit 4 and JUnit 5,
 * keeping the two concerns orthogonal.</p>
 *
 * @see SimulatestSession
 */
public interface SimulatestPlugin {

	/**
	 * Returns a custom {@link EnvironmentFactory} for creating environments via
	 * dependency injection, or {@code null} to use the default reflection-based
	 * factory. When multiple plugins are present, the first non-null factory wins.
	 *
	 * @return a custom environment factory, or {@code null} for the default
	 */
	default EnvironmentFactory environmentFactory() {
		return null;
	}

	/**
	 * Called once before the suite runs. Use this for container bootstrap
	 * and schema setup.
	 *
	 * @param testClasses all test classes discovered in the suite
	 */
	default void initialize(Collection<Class<?>> testClasses) {
	}

	/**
	 * Called once after the suite completes. Use this for container shutdown
	 * and resource cleanup.
	 */
	default void destroy() {
	}

	/**
	 * Creates a test instance via dependency injection instead of reflection.
	 * Return {@link Optional#empty()} to fall back to default construction.
	 *
	 * @param testClass the test class to instantiate
	 * @return a new test instance, or empty for default construction
	 */
	default Optional<Object> createTestInstance(Class<?> testClass) {
		return Optional.empty();
	}

	/**
	 * Post-processes a test instance after creation (e.g., field injection).
	 * Called regardless of which factory created the instance.
	 *
	 * @param instance the test instance to post-process
	 */
	default void postProcessTestInstance(Object instance) {
	}

	/**
	 * Relative position in the plugin run order: lower runs first, and plugins
	 * with equal order keep their {@link java.util.ServiceLoader} discovery
	 * order.
	 *
	 * <p>Override this when the plugin must run after others, rather than
	 * relying on where it appears in a {@code META-INF/services} file. The
	 * scale the built-in plugins use:</p>
	 * <ul>
	 *   <li><b>0</b> (the default) for plugins that supply a {@code DataSource},
	 *       such as the Spring, Guice and CDI plugins.</li>
	 *   <li><b>100</b> for {@code DatabaseBootstrapPlugin}, so a DataSource
	 *       configured by a DI plugin wins over the one it would supply itself.</li>
	 *   <li><b>200 and above</b> for plugins that need a DataSource already
	 *       configured, whichever plugin configured it.</li>
	 * </ul>
	 *
	 * @return the run order; {@code 0} by default
	 */
	default int order() {
		return 0;
	}

}
