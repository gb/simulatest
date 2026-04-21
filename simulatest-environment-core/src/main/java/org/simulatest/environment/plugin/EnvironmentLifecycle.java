package org.simulatest.environment.plugin;

import org.simulatest.environment.EnvironmentDefinition;

/**
 * Strategy that decides what the Simulatest engine does as it enters and exits
 * each node of the environment tree.
 *
 * <p>The default, {@link EagerEnvironmentLifecycle}, runs the environment and
 * pushes an Insistence Layer level on entry, pops the level on exit. Plugins
 * whose DI containers are not yet available when the tree walks (Quarkus,
 * for instance) return a different lifecycle whose {@link #onEnter} is a
 * no-op and whose level push is done later from inside the inner test
 * session.
 *
 * <p>Implementations receive an {@link EnvironmentExecution} callback object
 * that exposes the small slice of engine functionality they need, keeping
 * lifecycles free of any junit-platform coupling.
 */
public interface EnvironmentLifecycle {

	/** Invoked when the engine enters {@code definition}'s subtree. */
	void onEnter(EnvironmentDefinition definition, EnvironmentExecution execution);

	/** Invoked when the engine exits {@code definition}'s subtree. */
	void onExit(EnvironmentDefinition definition, EnvironmentExecution execution);

}
