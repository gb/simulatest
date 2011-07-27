package objectivelabs.environmentTestHarness.test;

import static org.junit.Assert.assertEquals;
import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaEnvironment;

import org.junit.Test;

public class EnvironmentDefinitionTest {

	@Test
	public void shouldReturnEnvironmentClass() {
		EnvironmentDefinition definition = EnvironmentDefinition.create(PessoaEnvironment.class);
		assertEquals(PessoaEnvironment.class, definition.getEnvironmentClass());
	}
	
	@Test
	public void shouldReturnName() {
		EnvironmentDefinition definition = EnvironmentDefinition.create(PessoaEnvironment.class);
		assertEquals("PessoaEnvironment", definition.getName());
	}
	
	@Test
	public void shouldReturnToString() {
		EnvironmentDefinition definition = EnvironmentDefinition.create(PessoaEnvironment.class);
		assertEquals("PessoaEnvironment", definition.toString());
	}
	
}