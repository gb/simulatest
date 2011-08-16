package org.simulatest.springrunner.junit;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.springrunner.environment.EnvironmentSpringFactory;
import org.simulatest.springrunner.spring.SpringContext;

public class SimulatestSpringRunner extends EnvironmentJUnitRunner {
	
	public SimulatestSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		SpringContext.initializeSpring();
	}
	
	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SimulatestSpringJUnit4Runner(this, test);
	}
	
	@Override
	public void run(final RunNotifier notifier) {
		super.run(notifier);
		SpringContext.destroy();
	}
	
	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentSpringFactory();
	}
	
}