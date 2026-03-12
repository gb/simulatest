package org.simulatest.environment.environment.listener;

import org.simulatest.environment.environment.EnvironmentDefinition;

public interface EnvironmentRunnerListener {

	default void beforeRun(EnvironmentDefinition definition) { }

	default void afterRun(EnvironmentDefinition definition) { }

	default void afterChildrenRun(EnvironmentDefinition definition) { }

	default void afterSiblingCleanup(EnvironmentDefinition definition) { }

	default ListenerPhase getPhase() {
		return ListenerPhase.APPLICATION;
	}

}
