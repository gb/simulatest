package objectiveLabs.environmentTestHarness.junit;

import objectiveLabs.environmentTestHarness.environment.EnvironmentFactory;
import objectiveLabs.environmentTestHarness.environment.EnvironmentSpringFactory;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerBuilder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

public class EnvironmentSpringSuiteRunner extends EnvironmentJUnitSuite {

	public EnvironmentSpringSuiteRunner(Class<?> suiteClass, RunnerBuilder builder) throws InitializationError {
		super(suiteClass, builder);
	}

	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SpringJUnit4ClassRunner(test);
	}
	
	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentSpringFactory();
	}
	
}