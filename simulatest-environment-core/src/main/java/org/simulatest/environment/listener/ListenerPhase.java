package org.simulatest.environment.listener;

/**
 * Determines the firing order of {@link EnvironmentRunnerListener} instances.
 * Infrastructure listeners always fire before application listeners (test
 * execution, custom logic).
 *
 * <p>The phase only orders listeners. The levels of the Insistence Layer are
 * not driven by a listener: the runner opens and closes them around the
 * events.</p>
 */
public enum ListenerPhase {
	INFRASTRUCTURE,
	APPLICATION
}
