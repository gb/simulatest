package org.simulatest.springrunner.junit;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.springrunner.environment.EnvironmentSpringFactory;
import org.simulatest.springrunner.spring.SpringContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class EnvironmentSpringRunner extends EnvironmentJUnitRunner {

	public EnvironmentSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
		SpringContext.initializeSpring();
	}
	
	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SpringJUnit4ClassRunner(test) {
			@Override
			protected void runChild(FrameworkMethod frameworkMethod, RunNotifier notifier) {
				super.runChild(frameworkMethod, notifier);
				getEnvironmentRunner().insistenceLayer().resetCurrentLevel();
			}
		};
	}
	
	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentSpringFactory();
	}
	
}