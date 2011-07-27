package org.simulatest.environment.environment;

public interface EnvironmentRunnerListener {

	void beforeRun(EnvironmentDefinition definition);
	
	void afterRun(EnvironmentDefinition definition);
	
	void afterChildrenRun(EnvironmentDefinition definition);
	
}