package org.simulatest.environment.junit5;

import org.simulatest.environment.Environment;
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
 * <p>Exit pops the savepoint only when the coordinator confirms one was
 * actually pushed for this environment. A claim that never led to a push
 * (the extension failed before pushing, a Quarkus restart wiped state, etc.)
 * leaves the stack alone, which prevents popping a savepoint that doesn't
 * exist.
 */
public final class DeferredEnvironmentLifecycle implements EnvironmentLifecycle {

	/** Stateless singleton; callers should prefer this over {@code new}. */
	public static final DeferredEnvironmentLifecycle INSTANCE = new DeferredEnvironmentLifecycle();

	@Override
	public void onEnter(EnvironmentDefinition definition, EnvironmentExecution execution) {
		// Intentionally empty. See class javadoc.
	}

	@Override
	public void onExit(EnvironmentDefinition definition, EnvironmentExecution execution) {
		Class<? extends Environment> environmentClass = definition.getEnvironmentClass();
		if (DeferredEnvironmentCoordinator.wasPushed(environmentClass)) {
			execution.decreaseInsistenceLevel();
		}
		DeferredEnvironmentCoordinator.forget(environmentClass);
	}

}
