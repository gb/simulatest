package org.simulatest.environment.test.testdouble;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.test.TestSetup;

// One environment that fails, with a child environment, next to one that works.
@RunWith(EnvironmentJUnitSuite.class)
@SuiteClasses({
		FailingEnvironmentTestDouble.class,
		ChildOfFailingEnvironmentTestDouble.class,
		InsistenceIsolationTestDouble.class })
public class FailingEnvironmentSuite {

	static { TestSetup.configure(); }

}
