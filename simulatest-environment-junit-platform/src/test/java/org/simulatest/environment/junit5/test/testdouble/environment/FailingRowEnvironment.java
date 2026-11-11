package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.junit5.test.testdouble.EnvRows;

/**
 * Test double: writes a row and then fails, like an environment that breaks
 * halfway through its setup.
 */
@EnvironmentParent(ParentRowEnvironment.class)
public class FailingRowEnvironment implements Environment {

	@Override
	public void run() {
		EnvRows.insert(3, "half-built");
		throw new IllegalStateException("environment failure");
	}

}
