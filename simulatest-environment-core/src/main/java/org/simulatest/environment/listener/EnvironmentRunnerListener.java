package org.simulatest.environment.listener;

import org.simulatest.environment.EnvironmentDefinition;

/**
 * Receives lifecycle callbacks during environment tree traversal.
 *
 * <p>Listeners fire in phase order ({@link ListenerPhase#INFRASTRUCTURE}
 * before {@link ListenerPhase#APPLICATION}). All listeners fire even if one
 * throws; exceptions are aggregated.</p>
 *
 * @see ListenerPhase
 */
public interface EnvironmentRunnerListener {

	/**
	 * Called before the environment's {@code run()} method executes.
	 *
	 * @param definition the environment about to run
	 */
	default void beforeRun(EnvironmentDefinition definition) { }

	/**
	 * Called after {@code run()} completes. This is where test execution
	 * typically hooks in. Not called for an environment that failed, so its
	 * tests do not run.
	 *
	 * @param definition the environment that just ran
	 */
	default void afterRun(EnvironmentDefinition definition) { }

	/**
	 * Called after all children of the environment have finished and before
	 * the level on top of the environment closes: the data of the environment
	 * is there, and so is what its last child wrote. Called for every
	 * environment that got {@code afterRun}, also when a child or a listener
	 * failed after it.
	 *
	 * @param definition the parent environment whose children have all run
	 */
	default void afterChildrenRun(EnvironmentDefinition definition) { }

	/**
	 * Called between sibling subtrees, after the data of the sibling that just
	 * finished was undone, so the next sibling starts from the same state.
	 *
	 * @param definition the parent of the siblings
	 */
	default void afterSiblingCleanup(EnvironmentDefinition definition) { }

	/**
	 * Returns the listener's phase, controlling firing order. Default is
	 * {@link ListenerPhase#APPLICATION}.
	 *
	 * @return the phase for this listener
	 */
	default ListenerPhase getPhase() {
		return ListenerPhase.APPLICATION;
	}

}
