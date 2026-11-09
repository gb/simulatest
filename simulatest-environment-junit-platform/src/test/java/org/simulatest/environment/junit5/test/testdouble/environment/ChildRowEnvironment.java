package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.junit5.test.testdouble.EnvRows;

@EnvironmentParent(ParentRowEnvironment.class)
public class ChildRowEnvironment implements Environment {

	@Override
	public void run() {
		EnvRows.insert(2, "child");
	}

}
