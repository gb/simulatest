package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.mock.Environments.PessoaEnvironment;


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