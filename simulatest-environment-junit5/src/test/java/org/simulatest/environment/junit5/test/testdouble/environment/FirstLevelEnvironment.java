package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.junit5.test.testdouble.EnvironmentTracker;

public class FirstLevelEnvironment implements Environment {

	@Override
	public void run() {
		EnvironmentTracker.record("FirstLevel");
	}

}
