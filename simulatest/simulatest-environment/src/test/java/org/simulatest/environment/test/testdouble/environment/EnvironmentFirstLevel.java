package org.simulatest.environment.test.testdouble.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.test.testdouble.DatabaseMock;

public class EnvironmentFirstLevel implements Environment {

	@Override
	public void run() {
		DatabaseMock.reset();
		DatabaseMock.addMessage("first");
	}

}
