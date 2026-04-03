package org.simulatest.environment.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.simulatest.environment.EnvironmentDefinition.create;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.BigBangEnvironment;
import org.simulatest.environment.tree.EnvironmentTreeBuilder;
import org.simulatest.environment.infra.exception.EnvironmentCyclicException;
import org.simulatest.environment.test.testdouble.Environments.ColaboradorEnvironment;
import org.simulatest.environment.test.testdouble.Environments.CyclicEnvironmentA;
import org.simulatest.environment.test.testdouble.Environments.CyclicEnvironmentOne;
import org.simulatest.environment.test.testdouble.Environments.EmpresaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.PessoaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.PessoaFisicaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.PessoaJuridicaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.ProjetoEnvironment;
import org.simulatest.environment.test.testdouble.Environments.ProjetoEnvironmentCiclico;
import org.simulatest.environment.test.testdouble.Environments.Root;

public class EnvironmentTreeBuilderTest {
	
	private EnvironmentTreeBuilder builder;
	
	@Before
	public void setup() {
		builder = new EnvironmentTreeBuilder();
	}
	
	@Test
	public void testPrint() {
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));
		builder.add(create(ProjetoEnvironment.class));
		
		String expectedPrint = 
			"-BigBangEnvironment\n" +
			"   -Root\n" +
			"      -PessoaEnvironment\n" +
			"         -PessoaFisicaEnvironment\n" +
			"            -ColaboradorEnvironment\n" +
			"         -PessoaJuridicaEnvironment\n" +
			"            -EmpresaEnvironment\n" +
			"               -ProjetoEnvironment\n";

		assertEquals(expectedPrint, builder.getTree().print());
	}

	@Test
	public void testAddSingleRootEnvironment() {
		builder.add(create(Root.class));

		Object[] expectedDefinitions = new Object[] {
				create(BigBangEnvironment.class),
				create(Root.class)
		};
		
		Object[] definitions = builder.getTree().getValues().toArray();

		assertArrayEquals(expectedDefinitions, definitions);
	}
	

	@Test
	public void testAddSingleLeafEnvironment() {
		builder.add(create(EmpresaEnvironment.class));

		Object[] expectedDefinitions = new Object[] {
				create(BigBangEnvironment.class),
				create(Root.class),
				create(PessoaEnvironment.class),
				create(PessoaJuridicaEnvironment.class),
				create(EmpresaEnvironment.class)
		};
		
		Object[] definitions = builder.getTree().getValues().toArray();

		assertArrayEquals(expectedDefinitions, definitions);
	}
	

	@Test
	public void testAddMultiplesEnvironments() {
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));
		builder.add(create(ProjetoEnvironment.class));

		Object[] expectedDefinitions = new Object[] {
				create(BigBangEnvironment.class),
				create(Root.class),
				create(PessoaEnvironment.class),
				create(PessoaFisicaEnvironment.class),
				create(ColaboradorEnvironment.class),
				create(PessoaJuridicaEnvironment.class),
				create(EmpresaEnvironment.class),
				create(ProjetoEnvironment.class)
		};
		
		Object[] definitions = builder.getTree().getValues().toArray();

		assertArrayEquals(expectedDefinitions, definitions);
	}
	
	@Test
	public void testSimpleCyclicEnvironment() {
		try {
			builder.add(create(ProjetoEnvironmentCiclico.class));
			fail("should throw an EnvironmentCyclicException");
		} catch (EnvironmentCyclicException exception) {
			assertTrue(exception.getMessage().startsWith("The environment \"ProjetoEnvironmentCiclico\" is cyclically referenced"));
			assertTrue(exception.getMessage().contains("visited:"));
		}
	}
	
	@Test
	public void testCyclicEnvironment() {
		try {
			builder.add(create(CyclicEnvironmentOne.class));
			fail("should throw an EnvironmentCyclicException");
		} catch (EnvironmentCyclicException exception) {
			assertTrue(exception.getMessage().startsWith("The environment \"CyclicEnvironmentOne\" is cyclically referenced"));
			assertTrue(exception.getMessage().contains("visited:"));
		}
	}
	
	@Test
	public void testComplexCyclicEnvironment() {
		try {
			builder.add(create(CyclicEnvironmentA.class));
			fail("should throw an EnvironmentCyclicException");
		} catch (EnvironmentCyclicException exception) {
			assertTrue(exception.getMessage().startsWith("The environment \"CyclicEnvironmentA\" is cyclically referenced"));
			assertTrue(exception.getMessage().contains("visited:"));
		}
	}
	
}