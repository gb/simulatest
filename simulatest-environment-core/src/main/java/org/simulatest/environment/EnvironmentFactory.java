package org.simulatest.environment;

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

}