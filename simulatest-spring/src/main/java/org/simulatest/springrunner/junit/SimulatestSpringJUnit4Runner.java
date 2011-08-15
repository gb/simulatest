package org.simulatest.springrunner.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.junit.AbstractEnvironmentJUnitRunner;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

class SimulatestSpringJUnit4Runner extends SpringJUnit4ClassRunner {
	
	private AbstractEnvironmentJUnitRunner runner;

	public SimulatestSpringJUnit4Runner(AbstractEnvironmentJUnitRunner runner, Class<?> clazz) throws InitializationError {
		super(clazz);
		this.runner = runner;
	}
	
	@Override
	protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
		super.runChild(frameworkMethod, notifier);
		runner.getEnvironmentRunner().insistenceLayer().resetCurrentLevel();
	}

}