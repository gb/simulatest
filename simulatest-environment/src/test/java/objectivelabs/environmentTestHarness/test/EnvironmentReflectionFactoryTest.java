package objectivelabs.environmentTestHarness.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import objectiveLabs.environmentTestHarness.environment.BigBangEnvironment;
import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectiveLabs.environmentTestHarness.environment.EnvironmentFactory;
import objectiveLabs.environmentTestHarness.environment.EnvironmentReflectionFactory;
import objectiveLabs.environmentTestHarness.infra.EnvironmentInstantiationException;
import objectivelabs.environmentTestHarness.mock.Environments.PrivateConstructorEnvironment;

import org.junit.Test;

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