package org.simulatest.jeerunner.junit;

import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.jeerunner.cdi.CdiContext;
import org.simulatest.jeerunner.environment.EnvironmentCdiFactory;

public class SimulatestJakartaRunner extends EnvironmentJUnitRunner {

	public SimulatestJakartaRunner(Class<?> testClass) throws InitializationError {
		super(testClass);
		CdiContext.initialize();
	}

	@Override
	protected EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentCdiFactory();
	}

	@Override
	protected Runner instanceTest(Class<?> test) throws InitializationError {
		return new SimulatestJakartaJUnit4Runner(this, test);
	}

	@Override
	public void run(RunNotifier notifier) {
		super.run(notifier);
		CdiContext.destroy();
	}

}
