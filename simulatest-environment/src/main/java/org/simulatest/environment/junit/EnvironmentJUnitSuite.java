package org.simulatest.environment.junit;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.TestCaseRaker;

public class EnvironmentJUnitSuite extends AbstractEnvironmentJUnitRunner {

	public EnvironmentJUnitSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(new TestCaseRaker(suiteClass).getAllTests());
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