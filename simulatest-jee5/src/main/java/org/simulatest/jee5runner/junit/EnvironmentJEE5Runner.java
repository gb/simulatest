package org.simulatest.jee5runner.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.jee5runner.environment.EnvironmentJEE5Factory;

public class EnvironmentJEE5Runner extends EnvironmentJUnitRunner {

	public EnvironmentJEE5Runner(Class<?> testClass) throws InitializationError {
		super(testClass);
	}
	
	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentJEE5Factory();
	}
	
	@Override
	public void run(final RunNotifier notifier) {
		super.run(notifier);
	}

}