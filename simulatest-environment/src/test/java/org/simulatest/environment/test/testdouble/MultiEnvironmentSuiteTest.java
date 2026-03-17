package org.simulatest.environment.test.testdouble;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.test.TestSetup;

@RunWith(EnvironmentJUnitSuite.class)
@SuiteClasses({ FirstLevelDummyTest.class, SecondLevelDummyTest.class })
public class MultiEnvironmentSuiteTest {

	static { TestSetup.configure(); }

}
