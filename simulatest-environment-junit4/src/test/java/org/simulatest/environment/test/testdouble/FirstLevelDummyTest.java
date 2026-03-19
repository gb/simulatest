package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;
import org.simulatest.environment.test.testdouble.environment.EnvironmentFirstLevel;

@UseEnvironment(EnvironmentFirstLevel.class)
public class FirstLevelDummyTest {

	static { TestSetup.configure(); }

	@Test
	public void testAtFirstLevel() {
		assertTrue(true);
	}

}
