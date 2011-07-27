package objectiveLabs.environmentTestHarness.junit;

import objectiveLabs.environmentTestHarness.environment.TestCaseRaker;

import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;

public class EnvironmentJUnitSuite extends AbstractEnvironmentJUnitRunner {

	public EnvironmentJUnitSuite(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(new TestCaseRaker(suiteClass).getAllTests());
	}

}