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
	 * Called after {@code run()} completes (or fails). This is where test
	 * execution typically hooks in.
	 *
	 * @param definition the environment that just ran
	 */
	default void afterRun(EnvironmentDefinition definition) { }

	/**
	 * Called after all children of the environment have finished. Used by the
	 * Insistence Layer to decrease the checkpoint level.
	 *
	 * @param definition the parent environment whose children have all run
	 */
	default void afterChildrenRun(EnvironmentDefinition definition) { }

	/**
	 * Called between sibling subtrees. Used by the Insistence Layer to reset
	 * the current checkpoint level so the next sibling starts from the same state.
	 *
	 * @param definition the sibling environment that just completed its subtree
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
