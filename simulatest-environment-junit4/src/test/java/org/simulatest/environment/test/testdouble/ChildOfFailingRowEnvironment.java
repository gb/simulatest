package org.simulatest.environment.test.testdouble;

import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;

// Never runs: its parent always fails.
@EnvironmentParent(FailingRowEnvironment.class)
public class ChildOfFailingRowEnvironment implements Environment {

	@Override
	public void run() {
		throw new UnsupportedOperationException("Should never be reached: the parent environment fails first");
	}

}
