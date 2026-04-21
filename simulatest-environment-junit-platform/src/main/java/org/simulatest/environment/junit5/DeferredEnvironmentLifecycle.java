package org.simulatest.environment.junit5;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.plugin.EnvironmentExecution;
import org.simulatest.environment.plugin.EnvironmentLifecycle;

/**
 * {@link EnvironmentLifecycle} that defers environment instantiation and
 * savepoint placement into the inner Jupiter session. The engine's
 * tree-walk entry is a no-op; a Jupiter extension shipped by the deferring
 * plugin runs the environment and pushes the savepoint after its DI
 * container is ready.
 *
 * <p>Exit pops the savepoint the extension pushed and clears the coordinator's
 * record for this environment, so a sibling or re-entry triggers a fresh run
 * rather than reusing now-rolled-back state.
 */
public final class DeferredEnvironmentLifecycle implements EnvironmentLifecycle {

	@Override
	public void onEnter(EnvironmentDefinition definition, EnvironmentExecution execution) {
		// Intentionally empty. See class javadoc.
	}

	@Override
	public void onExit(EnvironmentDefinition definition, EnvironmentExecution execution) {
		execution.decreaseInsistenceLevel();
		DeferredEnvironmentCoordinator.forget(definition.getEnvironmentClass());
	}

}
