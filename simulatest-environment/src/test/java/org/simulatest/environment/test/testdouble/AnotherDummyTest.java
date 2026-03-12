package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.simulatest.environment.test.TestSetup;

public class AnotherDummyTest {

	static { TestSetup.configure(); }

	@Test
	public void testTrue() {
		assertTrue(true);
	}
	
	@Test
	public void testFalse() {
		assertFalse(false);
	}

}
