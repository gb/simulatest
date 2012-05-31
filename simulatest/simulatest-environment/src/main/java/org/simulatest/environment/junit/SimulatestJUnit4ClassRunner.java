package org.simulatest.environment.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;

public class SimulatestJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private final AbstractEnvironmentJUnitRunner runner;
	
	public SimulatestJUnit4ClassRunner(AbstractEnvironmentJUnitRunner runner, Class<?> clazz) throws InitializationError {
		super(clazz);
		this.runner = runner;
	}
	
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		super.runChild(method, notifier);
		runner.getEnvironmentRunner().insistenceLayer().resetCurrentLevel();
	}

}
