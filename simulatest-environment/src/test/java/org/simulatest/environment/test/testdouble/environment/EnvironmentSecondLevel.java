package org.simulatest.environment.test.testdouble.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.test.testdouble.DatabaseMock;

@EnvironmentParent(value = EnvironmentFirstLevel.class)
public class EnvironmentSecondLevel implements Environment {

	@Override
	public void run() {
		DatabaseMock.addMessage("second");
	}

}
