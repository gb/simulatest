package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.Environment;
import org.simulatest.environment.junit5.test.testdouble.EnvironmentTracker;

@EnvironmentParent(FirstLevelEnvironment.class)
public class SecondLevelEnvironment implements Environment {

	@Override
	public void run() {
		EnvironmentTracker.trackEvent("SecondLevel");
	}

}
