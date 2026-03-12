package org.simulatest.environment.junit5.plugin;

import java.util.Collection;

import org.simulatest.environment.environment.EnvironmentFactory;

/**
 * SPI extension point for integrating DI frameworks (Spring, CDI, etc.)
 * with the Simulatest JUnit 5 TestEngine.
 *
 * <p>Implementations are loaded via {@link java.util.ServiceLoader}. At most one
 * plugin may provide a non-null {@link EnvironmentFactory}.</p>
 */
public interface SimulatestEnginePlugin {

	/**
	 * Returns a custom {@link EnvironmentFactory} for creating environment instances
	 * through a DI container, or {@code null} to use the default reflection-based factory.
	 */
	default EnvironmentFactory getEnvironmentFactory() {
		return null;
	}

	/**
	 * Called once before discovery, allowing the plugin to initialize its DI context.
	 *
	 * @param testClasses all test classes discovered by the engine
	 */
	default void initialize(Collection<Class<?>> testClasses) {
	}

	/**
	 * Called after all tests have executed, allowing the plugin to tear down its DI context.
	 */
	default void destroy() {
	}

	/**
	 * Creates a test class instance, or returns {@code null} to let the engine
	 * use default reflection-based instantiation.
	 */
	default Object createTestInstance(Class<?> clazz) throws Exception {
		return null;
	}

	/**
	 * Post-processes a test instance (e.g., autowiring fields) after creation.
	 */
	default void postProcessTestInstance(Object instance) {
	}

}
