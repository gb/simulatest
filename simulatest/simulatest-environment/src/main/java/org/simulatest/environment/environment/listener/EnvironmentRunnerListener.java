package org.simulatest.environment.environment.listener;

import org.simulatest.environment.environment.EnvironmentDefinition;

public interface EnvironmentRunnerListener {

	void beforeRun(EnvironmentDefinition definition);

	void afterRun(EnvironmentDefinition definition);

	void afterChildrenRun(EnvironmentDefinition definition);

}
