package org.simulatest.environment.plugin;

import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.SimulatestSession;

import java.util.Collection;

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
	 * Return {@code null} to fall back to default construction.
	 *
	 * @param testClass the test class to instantiate
	 * @return a new test instance, or {@code null} for default construction
	 */
	default Object createTestInstance(Class<?> testClass) {
		return null;
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
	 * Returns this plugin's {@link EnvironmentLifecycle}, which decides what
	 * happens when the Simulatest engine enters and exits an environment node
	 * in the test tree. Return {@code null} (the default) to use whichever
	 * lifecycle another plugin contributes, falling back to the eager tree-walk
	 * behavior if none is contributed.
	 *
	 * <p>Plugins backed by DI containers that are only available once a test
	 * class enters its JUnit lifecycle (e.g., Quarkus's Arc, bootstrapped by
	 * {@code QuarkusTestExtension#beforeAll}) return a deferred lifecycle so
	 * environment instantiation happens inside the inner Jupiter session.
	 *
	 * @return this plugin's lifecycle, or {@code null} to not contribute one
	 */
	default EnvironmentLifecycle environmentLifecycle() {
		return null;
	}

}
