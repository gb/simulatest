package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentExtractor;
import org.simulatest.environment.test.testdouble.Environments.PessoaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.Root;

public class EnvironmentExtractorTest {
	
	private EnvironmentExtractor environmentExtractor;
	private List<Class<?>> tests;
	
	@Before
	public void setup() {
		tests = new LinkedList<Class<?>>();
		
		tests.add(TestWithoutEnvironment1.class);
		tests.add(TestWithoutEnvironment2.class);
		
		tests.add(TestWithEnvironment1.class);
		tests.add(TestWithEnvironment2.class);
		tests.add(TestWithEnvironment3.class);
		tests.add(TestWithEnvironment4.class);
		
		environmentExtractor = EnvironmentExtractor.extract(tests);
	}
	
	@Test
	public void testEnvironmentMap() {
		assertEquals(3, environmentExtractor.size());
		
		List<Class<?>> classesWithoutEnvironment = environmentExtractor.getTests(EnvironmentDefinition.bigBang());
		
		assertEquals(2, classesWithoutEnvironment.size());
		assertTrue(classesWithoutEnvironment.contains(TestWithoutEnvironment1.class));
		assertTrue(classesWithoutEnvironment.contains(TestWithoutEnvironment2.class));

		List<Class<?>> classWithRootEnvironment = environmentExtractor.getTests(EnvironmentDefinition.create(Root.class));

		assertEquals(2, classWithRootEnvironment.size());
		assertTrue(classWithRootEnvironment.contains(TestWithEnvironment1.class));
		assertTrue(classWithRootEnvironment.contains(TestWithEnvironment2.class));

		List<Class<?>> classesWithPessoaEnvironment = environmentExtractor.getTests(EnvironmentDefinition.create(PessoaEnvironment.class));

		assertEquals(2, classesWithPessoaEnvironment.size());
		assertTrue(classesWithPessoaEnvironment.contains(TestWithEnvironment3.class));
		assertTrue(classesWithPessoaEnvironment.contains(TestWithEnvironment4.class));
	}
		
	static class TestWithoutEnvironment1 { }
	static class TestWithoutEnvironment2 { }

	@UseEnvironment(Root.class)
	static class TestWithEnvironment1 { }

	@UseEnvironment(Root.class)
	static class TestWithEnvironment2 { }

	@UseEnvironment(PessoaEnvironment.class)
	static class TestWithEnvironment3 { }

	@UseEnvironment(PessoaEnvironment.class)
	static class TestWithEnvironment4 { }

}