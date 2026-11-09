package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.Environment;
import org.simulatest.environment.junit5.test.testdouble.EnvRows;

public class ParentRowEnvironment implements Environment {

	@Override
	public void run() {
		EnvRows.insert(1, "parent");
	}

}
