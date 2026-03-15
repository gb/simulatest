package org.simulatest.environment.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.environment.SuiteTestExtractor;

public class SuiteTestExtractorTest {
	
	private SuiteTestExtractor suiteTestExtractor;
	
	@Before
	public void setup() {
		suiteTestExtractor = new SuiteTestExtractor(SuitePrincipal.class);
	}
	
	@Test
	public void suiteTestExtractorGetAllTests() {
		assertTrue(suiteTestExtractorContains(TestClassFake1.class));
		assertTrue(suiteTestExtractorContains(TestClassFake2.class));
		assertTrue(suiteTestExtractorContains(TestClassFake3.class));
		
		assertFalse(suiteTestExtractorContains(SuitePrincipal.class));
		assertFalse(suiteTestExtractorContains(SubSuite1.class));
		assertFalse(suiteTestExtractorContains(SubSuite2.class));
	}
		
	private boolean suiteTestExtractorContains(Class<?> clazz) {
		return suiteTestExtractor.getAllTests().contains(clazz);
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