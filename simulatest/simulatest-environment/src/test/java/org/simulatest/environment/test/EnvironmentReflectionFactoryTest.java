package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.simulatest.environment.environment.EnvironmentDefinition.create;

import org.junit.Test;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.simulatest.environment.test.testdouble.Environments.PrivateConstructorEnvironment;

public class EnvironmentReflectionFactoryTest {
	
	private EnvironmentFactory environmentReflection = new EnvironmentReflectionFactory();
	
	@Test
	public void shouldInstanceAnEnvironmentWithSuccess() {
		try {
			environmentReflection.create(create(BigBangEnvironment.class));
		} catch (Exception exception) {
			fail("should instance an environment successfully");
		}
	}

	@Test
	public void shouldThrowAnEnvironmentInstantiationExceptionWhenSomethingWrongHappen() {
		try {
			environmentReflection.create(create(PrivateConstructorEnvironment.class));
			fail("should throw an EnvironmentInstantiationException");
		} catch (EnvironmentInstantiationException exception) {
			String expectedMessage = "Error in instanciation of environment: PrivateConstructorEnvironment";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
}