package objectivelabs.environmentTestHarness.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import objectiveLabs.environmentTestHarness.environment.BigBangEnvironment;
import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectiveLabs.environmentTestHarness.environment.EnvironmentTreeBuilder;
import objectiveLabs.environmentTestHarness.infra.EnvironmentCyclicException;
import objectivelabs.environmentTestHarness.mock.Environments.ColaboradorEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.CyclicEnvironmentA;
import objectivelabs.environmentTestHarness.mock.Environments.CyclicEnvironmentOne;
import objectivelabs.environmentTestHarness.mock.Environments.EmpresaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaFisicaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaJuridicaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.ProjetoEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.ProjetoEnvironmentCiclico;
import objectivelabs.environmentTestHarness.mock.Environments.Root;

import org.junit.Before;
import org.junit.Test;

public class EnvironmentTreeBuilderTest {
	
	private EnvironmentTreeBuilder builder;
	
	@Before
	public void setup() {
		builder = new EnvironmentTreeBuilder();
	}
	
	@Test
	public void testPrint() {
		builder.add(EnvironmentDefinition.create(ColaboradorEnvironment.class));
		builder.add(EnvironmentDefinition.create(EmpresaEnvironment.class));
		builder.add(EnvironmentDefinition.create(ProjetoEnvironment.class));
		
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
		builder.add(EnvironmentDefinition.create(Root.class));

		Object[] expectedDefinitions = new Object[] {
				EnvironmentDefinition.create(BigBangEnvironment.class),
				EnvironmentDefinition.create(Root.class)
		};
		
		Object[] definitions = builder.getTree().getValues().toArray();

		assertArrayEquals(expectedDefinitions, definitions);
	}
	

	@Test
	public void testAddSingleLeafEnvironment() {
		builder.add(EnvironmentDefinition.create(EmpresaEnvironment.class));

		Object[] expectedDefinitions = new Object[] {
				EnvironmentDefinition.create(BigBangEnvironment.class),
				EnvironmentDefinition.create(Root.class),
				EnvironmentDefinition.create(PessoaEnvironment.class),
				EnvironmentDefinition.create(PessoaJuridicaEnvironment.class),
				EnvironmentDefinition.create(EmpresaEnvironment.class)
		};
		
		Object[] definitions = builder.getTree().getValues().toArray();

		assertArrayEquals(expectedDefinitions, definitions);
	}
	

	@Test
	public void testAddMultiplesEnvironments() {
		builder.add(EnvironmentDefinition.create(ColaboradorEnvironment.class));
		builder.add(EnvironmentDefinition.create(EmpresaEnvironment.class));
		builder.add(EnvironmentDefinition.create(ProjetoEnvironment.class));

		Object[] expectedDefinitions = new Object[] {
				EnvironmentDefinition.create(BigBangEnvironment.class),
				EnvironmentDefinition.create(Root.class),
				EnvironmentDefinition.create(PessoaEnvironment.class),
				EnvironmentDefinition.create(PessoaFisicaEnvironment.class),
				EnvironmentDefinition.create(ColaboradorEnvironment.class),
				EnvironmentDefinition.create(PessoaJuridicaEnvironment.class),
				EnvironmentDefinition.create(EmpresaEnvironment.class),
				EnvironmentDefinition.create(ProjetoEnvironment.class)
		};
		
		Object[] definitions = builder.getTree().getValues().toArray();

		assertArrayEquals(expectedDefinitions, definitions);
	}
	
	@Test
	public void testSimpleCyclicEnvironment() {
		try {
			builder.add(EnvironmentDefinition.create(ProjetoEnvironmentCiclico.class));
			fail("should throw an EnvironmentCyclicException");
		} catch (EnvironmentCyclicException exception) {
			String expectedMessage = "The environment \"ProjetoEnvironmentCiclico\" is cyclicity referenced";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
	@Test
	public void testCyclicEnvironment() {
		try {
			builder.add(EnvironmentDefinition.create(CyclicEnvironmentOne.class));
			fail("should throw an EnvironmentCyclicException");
		} catch (EnvironmentCyclicException exception) {
			String expectedMessage = "The environment \"CyclicEnvironmentOne\" is cyclicity referenced";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
	@Test
	public void testComplexCyclicEnvironment() {
		try {
			builder.add(EnvironmentDefinition.create(CyclicEnvironmentA.class));
			fail("should throw an EnvironmentCyclicException");
		} catch (EnvironmentCyclicException exception) {
			String expectedMessage = "The environment \"CyclicEnvironmentA\" is cyclicity referenced";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
}