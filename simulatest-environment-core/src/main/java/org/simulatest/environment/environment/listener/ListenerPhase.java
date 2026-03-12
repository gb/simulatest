package org.simulatest.environment.environment.listener;

/**
 * Determines the firing order of {@link EnvironmentRunnerListener} instances.
 * Infrastructure listeners (savepoints, transactions) always fire before
 * application listeners (test execution, custom logic).
 */
public enum ListenerPhase {
	INFRASTRUCTURE,
	APPLICATION
}
