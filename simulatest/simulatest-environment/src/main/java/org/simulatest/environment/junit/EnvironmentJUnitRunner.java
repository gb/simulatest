package org.simulatest.environment.junit;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;

public class EnvironmentJUnitRunner extends AbstractEnvironmentJUnitRunner {

	public EnvironmentJUnitRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}

	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentReflectionFactory();
	}

	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SimulatestJUnit4ClassRunner(this, test);
	}

}