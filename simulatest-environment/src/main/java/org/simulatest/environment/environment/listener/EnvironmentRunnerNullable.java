package org.simulatest.environment.environment.listener;

import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentRunnerListener;

public class EnvironmentRunnerNullable implements EnvironmentRunnerListener {

	@Override
	public void beforeRun(EnvironmentDefinition definition) { }

	@Override
	public void afterRun(EnvironmentDefinition definition) { }

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) { }

}