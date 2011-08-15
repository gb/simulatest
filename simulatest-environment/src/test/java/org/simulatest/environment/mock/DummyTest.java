package org.simulatest.environment.mock;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DummyTest {
	
	@Test
	public void testSum() {
		assertEquals(2, 1 + 1);
	}
	
	@Test
	public void testSubtract() {
		assertEquals(2, 4 - 2);
	}

}
