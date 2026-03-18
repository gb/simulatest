package org.simulatest.environment.environment.plugin;

import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.SimulatestSession;

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

	default EnvironmentFactory environmentFactory() {
		return null;
	}

	default void initialize(Collection<Class<?>> testClasses) {
	}

	default void destroy() {
	}

	default Object createTestInstance(Class<?> testClass) {
		return null;
	}

	default void postProcessTestInstance(Object instance) {
	}

}
