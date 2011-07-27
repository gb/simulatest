package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.infra.EnvironmentInstantiationException;
import org.simulatest.environment.mock.Environments.PrivateConstructorEnvironment;


public class EnvironmentReflectionFactoryTest {
	
	private EnvironmentFactory environmentReflection = new EnvironmentReflectionFactory();
	
	@Test
	public void shouldInstanceAnEnvironmentWithSuccess() {
		try {
			environmentReflection.create(EnvironmentDefinition.create(BigBangEnvironment.class));
		} catch (Exception exception) {
			fail("should instance an environment successfully");
		}
	}

	@Test
	public void shouldThrowAnEnvironmentInstantiationExceptionWhenSomethingWrongHappen() {
		try {
			environmentReflection.create(EnvironmentDefinition.create(PrivateConstructorEnvironment.class));
			fail("should throw an EnvironmentInstantiationException");
		} catch (EnvironmentInstantiationException exception) {
			String expectedMessage = "Error in instanciation of environment: PrivateConstructorEnvironment";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
}