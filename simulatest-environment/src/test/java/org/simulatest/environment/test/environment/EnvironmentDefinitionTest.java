package org.simulatest.environment.test.environment;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.mock.Environments.PessoaEnvironment;

public class EnvironmentDefinitionTest {
	
	EnvironmentDefinition definition = EnvironmentDefinition.create(PessoaEnvironment.class);

	@Test
	public void shouldReturnTheEnvironmentClassWhenCallGetEnvironmentClass() {
		assertEquals(PessoaEnvironment.class, definition.getEnvironmentClass());
	}
	
	@Test
	public void shouldReturnTheEnvironmentSimpleClassNameWhenCallGetName() {
		assertEquals("PessoaEnvironment", definition.getName());
	}
	
	@Test
	public void shouldReturnTheEnvironmentDefinitionNameWhenCallToString() {
		assertEquals("PessoaEnvironment", definition.toString());
	}
	
}