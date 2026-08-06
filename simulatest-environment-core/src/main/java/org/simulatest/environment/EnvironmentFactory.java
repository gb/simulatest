package org.simulatest.environment;

import org.simulatest.environment.infra.exception.EnvironmentExecutionException;

/**
 * Creates {@link Environment} instances from their definitions.
 *
 * <p>The default implementation uses reflection. DI plugins (Spring, Guice,
 * CDI) provide their own factory so that dependency injection is available
 * inside environments.</p>
 *
 * @see org.simulatest.environment.plugin.SimulatestPlugin#environmentFactory()
 */
public interface EnvironmentFactory {

	/**
	 * Creates an environment instance for the given definition.
	 *
	 * @param definition the environment definition to instantiate
	 * @return a new {@link Environment} instance
	 */
	Environment create(EnvironmentDefinition definition);

	/**
	 * Creates the environment for {@code definition} and runs it, wrapping any
	 * failure in an {@link EnvironmentExecutionException} naming the environment.
	 *
	 * <p>Shared by every runner so that all integrations report environment
	 * failures the same way.</p>
	 *
	 * @param definition the environment definition to instantiate and run
	 */
	default void createAndRun(EnvironmentDefinition definition) {
		try {
			create(definition).run();
		} catch (Exception exception) {
			throw new EnvironmentExecutionException(
					"Failed during run for environment '" + definition.getName() + "'", exception);
		}
	}

}