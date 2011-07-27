package org.simulatest.springrunner.junit;


import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.springrunner.environment.EnvironmentSpringFactory;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


public class EnvironmentSpringRunner extends EnvironmentJUnitRunner {

	private EnvironmentSpringFactory environmentSpringFactory = new EnvironmentSpringFactory();

	public EnvironmentSpringRunner(Class<?> clazz) throws InitializationError {
		super(clazz);
	}
	
	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SpringJUnit4ClassRunner(test);
	}
	
	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return environmentSpringFactory;
	}
	
}