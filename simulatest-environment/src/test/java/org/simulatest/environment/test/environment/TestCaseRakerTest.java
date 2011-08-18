package org.simulatest.environment.test.environment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.environment.TestCaseRaker;

public class TestCaseRakerTest {
	
	private TestCaseRaker testCaseRaker;
	
	@Before
	public void setup() {
		testCaseRaker = new TestCaseRaker(SuitePrincipal.class);
	}
	
	@Test
	public void testCaseRakerGetAllTests() {
		assertTrue(testCaseRakerContains(TestClassFake1.class));
		assertTrue(testCaseRakerContains(TestClassFake2.class));
		assertTrue(testCaseRakerContains(TestClassFake3.class));
		
		assertFalse(testCaseRakerContains(SuitePrincipal.class));
		assertFalse(testCaseRakerContains(SubSuite1.class));
		assertFalse(testCaseRakerContains(SubSuite2.class));
	}
		
	private boolean testCaseRakerContains(Class<?> clazz) {
		return testCaseRaker.getAllTests().contains(clazz);
	}
	
	@SuiteClasses(value = { SubSuite1.class, SubSuite2.class })
	class SuitePrincipal { }
	
	@SuiteClasses(value = { TestClassFake1.class })
	class SubSuite1 { }
	
	@SuiteClasses(value = { TestClassFake2.class, TestClassFake3.class })
	class SubSuite2 { }
	
	class TestClassFake1 { }
	class TestClassFake2 { }
	class TestClassFake3 { }

}