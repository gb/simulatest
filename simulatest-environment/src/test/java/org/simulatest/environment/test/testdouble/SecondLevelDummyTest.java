package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;
import org.simulatest.environment.test.testdouble.environment.EnvironmentSecondLevel;

@UseEnvironment(EnvironmentSecondLevel.class)
public class SecondLevelDummyTest {

	static { TestSetup.configure(); }

	@Test
	public void testAtSecondLevel() {
		assertTrue(true);
	}

}
