package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;

@RunWith(EnvironmentJUnitRunner.class)
public class AnotherDummyTest {
	
	@Test
	public void testTrue() {
		assertTrue(true);
	}
	
	@Test
	public void testFalse() {
		assertFalse(false);
	}

}
