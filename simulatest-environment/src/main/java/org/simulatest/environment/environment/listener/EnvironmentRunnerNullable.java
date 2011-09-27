package org.simulatest.environment.environment.listener;

import org.simulatest.environment.environment.EnvironmentDefinition;

public class EnvironmentRunnerNullable implements EnvironmentRunnerListener {

	@Override
	public void beforeRun(EnvironmentDefinition definition) { }

	@Override
	public void afterRun(EnvironmentDefinition definition) { }

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) { }

}