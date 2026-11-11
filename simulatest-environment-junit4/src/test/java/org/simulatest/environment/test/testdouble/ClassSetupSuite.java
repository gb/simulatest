package org.simulatest.environment.test.testdouble;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.test.TestSetup;

// The class with a class setup runs first; the class after it shares the
// environment and expects to see only the environment row.
@RunWith(EnvironmentJUnitSuite.class)
@SuiteClasses({ ClassSetupTestDouble.class, InsistenceIsolationTestDouble.class })
public class ClassSetupSuite {

	static { TestSetup.configure(); }

}
