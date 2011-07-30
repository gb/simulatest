package org.simulatest.environment.junit;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.simulatest.environment.environment.TestCaseRaker;

public class EnvironmentJUnitSuite extends AbstractEnvironmentJUnitRunner {

	public EnvironmentJUnitSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(new TestCaseRaker(suiteClass).getAllTests());
	}

}