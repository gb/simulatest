package org.simulatest.environment.junit;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.simulatest.environment.environment.SuiteTestExtractor;

public class EnvironmentJUnitSuite extends AbstractEnvironmentJUnitRunner {

	public EnvironmentJUnitSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(new SuiteTestExtractor(suiteClass).getAllTests());
	}

}
