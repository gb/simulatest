package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.test.testdouble.Environments.PessoaEnvironment;

public class EnvironmentDefinitionTest {
	
	EnvironmentDefinition definition = EnvironmentDefinition.create(PessoaEnvironment.class);

	@Test
	public void shouldReturnTheEnvironmentClassWhenCallGetEnvironmentClass() {
		assertEquals(PessoaEnvironment.class, definition.getEnvironmentClass());
	}
	
	@Test
	public void shouldReturnTheEnvironmentSimpleClassNameWhenCallGetName() {
		assertEquals(PessoaEnvironment.class.getSimpleName(), definition.getName());
	}

	@Test
	public void shouldReturnTheEnvironmentDefinitionNameWhenCallToString() {
		assertEquals(PessoaEnvironment.class.getSimpleName(), definition.toString());
	}
	
}