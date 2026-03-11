package org.simulatest.springrunner.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.junit.AbstractEnvironmentJUnitRunner;
import org.simulatest.springrunner.spring.SpringContext;

class SimulatestSpringJUnit4Runner extends BlockJUnit4ClassRunner {

	private final AbstractEnvironmentJUnitRunner runner;

	SimulatestSpringJUnit4Runner(AbstractEnvironmentJUnitRunner runner, Class<?> clazz) throws InitializationError {
		super(clazz);
		this.runner = runner;
	}

	@Override
	protected Object createTest() throws Exception {
		Object testInstance = super.createTest();
		SpringContext.autowire(testInstance);
		return testInstance;
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		super.runChild(method, notifier);
		runner.getEnvironmentRunner().insistenceLayer().resetCurrentLevel();
	}

}
