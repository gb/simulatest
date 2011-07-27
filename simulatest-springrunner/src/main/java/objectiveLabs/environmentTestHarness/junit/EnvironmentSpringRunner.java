package objectiveLabs.environmentTestHarness.junit;

import objectiveLabs.environmentTestHarness.environment.EnvironmentFactory;
import objectiveLabs.environmentTestHarness.environment.EnvironmentSpringFactory;

import org.junit.runner.Runner;
import org.junit.runners.model.InitializationError;
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