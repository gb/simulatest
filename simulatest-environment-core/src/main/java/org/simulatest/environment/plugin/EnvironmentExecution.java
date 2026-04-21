package org.simulatest.environment.plugin;

import org.simulatest.environment.EnvironmentDefinition;

/**
 * Narrow callback surface exposed to an {@link EnvironmentLifecycle} during
 * the engine's tree walk. Lifecycles implementing the interface depend only
 * on this surface, not on the wider JUnit engine, which keeps them portable
 * between the JUnit 4 and JUnit Platform engines.
 */
public interface EnvironmentExecution {

	/** Instantiate and run the environment via the configured factory. */
	void runEnvironment(EnvironmentDefinition definition);

	/** Push a new Insistence Layer level (creates a savepoint). */
	void increaseInsistenceLevel();

	/** Pop the current Insistence Layer level (rolls back to its savepoint). */
	void decreaseInsistenceLevel();

}
