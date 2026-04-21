package org.simulatest.environment.plugin;

import org.simulatest.environment.EnvironmentDefinition;

/**
 * Default {@link EnvironmentLifecycle}: runs the environment and pushes a
 * savepoint on entry, pops the savepoint on exit. Matches the classic tree-walk
 * behavior the Simulatest engine has always had, and is used when no plugin
 * contributes a different lifecycle.
 */
public final class EagerEnvironmentLifecycle implements EnvironmentLifecycle {

	@Override
	public void onEnter(EnvironmentDefinition definition, EnvironmentExecution execution) {
		execution.runEnvironment(definition);
		execution.increaseInsistenceLevel();
	}

	@Override
	public void onExit(EnvironmentDefinition definition, EnvironmentExecution execution) {
		execution.decreaseInsistenceLevel();
	}

}
