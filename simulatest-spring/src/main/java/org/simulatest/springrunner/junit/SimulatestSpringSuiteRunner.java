package org.simulatest.springrunner.junit;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.springrunner.environment.EnvironmentSpringFactory;
import org.simulatest.springrunner.spring.SpringContext;

public class SimulatestSpringSuiteRunner extends EnvironmentJUnitSuite {

	public SimulatestSpringSuiteRunner(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(suiteClass, builder);
		SpringContext.initializeSpring();
	}

	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SimulatestSpringJUnit4Runner(this, test);
	}
	
	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentSpringFactory();
	}
	
}