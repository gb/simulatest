package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;

@RunWith(EnvironmentJUnitRunner.class)
public class DummyTest {
	
	@Test
	public void testSum() {
		assertEquals(2, 1 + 1);
	}
	
	@Test
	public void testSubtract() {
		assertEquals(2, 4 - 2);
	}
	
	@Test
	public void testMultiply() {
		assertEquals(4, 2 * 2);
	}
	
	@Test
	public void testDivision() {
		assertEquals(2, 4 / 2);
	}

}