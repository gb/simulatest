
package objectivelabs.environmentTestHarness.test;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

import java.util.LinkedList;
import java.util.List;

import objectiveLabs.environmentTestHarness.annotation.UseEnvironment;
import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectiveLabs.environmentTestHarness.environment.EnvironmentRaker;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.Root;

import org.junit.Before;
import org.junit.Test;

public class EnvironmentRakerTest {
	
	private EnvironmentRaker environmentRaker;
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
		
		environmentRaker = new EnvironmentRaker(tests);
	}
	
	@Test
	public void testEnvironmentMap() {
		assertEquals(3, environmentRaker.size());
		
		List<Class<?>> classesWithoutEnvironment = environmentRaker.getTests(EnvironmentDefinition.bigBang());
		
		assertTrue(classesWithoutEnvironment.size() == 2);
		assertTrue(classesWithoutEnvironment.contains(TestWithoutEnvironment1.class));
		assertTrue(classesWithoutEnvironment.contains(TestWithoutEnvironment2.class));
		
		List<Class<?>> classWithRootEnvironment = environmentRaker.getTests(EnvironmentDefinition.create(Root.class));
		
		assertTrue(classWithRootEnvironment.size() == 2);
		assertTrue(classWithRootEnvironment.contains(TestWithEnvironment1.class));
		assertTrue(classWithRootEnvironment.contains(TestWithEnvironment2.class));
		
		List<Class<?>> classesWithPessoaEnvironment = environmentRaker.getTests(EnvironmentDefinition.create(PessoaEnvironment.class));
		
		assertTrue(classesWithPessoaEnvironment.size() == 2);
		assertTrue(classesWithPessoaEnvironment.contains(TestWithEnvironment3.class));
		assertTrue(classesWithPessoaEnvironment.contains(TestWithEnvironment4.class));
	}
		
	class TestWithoutEnvironment1 { }
	class TestWithoutEnvironment2 { }
	
	@UseEnvironment(Root.class)
	class TestWithEnvironment1 { }
	
	@UseEnvironment(Root.class)
	class TestWithEnvironment2 { }
	
	@UseEnvironment(PessoaEnvironment.class)
	class TestWithEnvironment3 { }
	
	@UseEnvironment(PessoaEnvironment.class)
	class TestWithEnvironment4 { }

}
