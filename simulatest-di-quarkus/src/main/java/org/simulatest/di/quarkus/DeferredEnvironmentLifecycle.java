package org.simulatest.di.quarkus;

import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.plugin.EnvironmentExecution;
import org.simulatest.environment.plugin.EnvironmentLifecycle;

/**
 * {@link EnvironmentLifecycle} that defers environment instantiation and
 * savepoint placement into Quarkus's inner Jupiter session. The engine's
 * tree-walk entry is a no-op; {@link PostArcEnvironmentRunner} runs the
 * environment and pushes the savepoint after Arc has booted.
 *
 * <p>Exit pops the savepoint only when the coordinator confirms one was
 * actually pushed for this environment. A claim that never led to a push
 * (the extension failed before pushing, a Quarkus restart wiped state, etc.)
 * leaves the stack alone, preventing a pop of a savepoint that doesn't exist.
 *
 * <p>Package-private; only {@link SimulatestQuarkusPlugin} contributes it.
 */
final class DeferredEnvironmentLifecycle implements EnvironmentLifecycle {

	/** Stateless singleton. */
	static final DeferredEnvironmentLifecycle INSTANCE = new DeferredEnvironmentLifecycle();

	private DeferredEnvironmentLifecycle() {}

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
