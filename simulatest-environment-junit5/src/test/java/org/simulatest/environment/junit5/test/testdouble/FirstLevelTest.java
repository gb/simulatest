package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

@UseEnvironment(FirstLevelEnvironment.class)
public class FirstLevelTest {

	@Test
	public void shouldHaveFirstLevelEnvironment() {
		Assertions.assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"));
	}

	@Test
	public void shouldNotHaveSecondLevel() {
		Assertions.assertFalse(EnvironmentTracker.getEvents().contains("SecondLevel"));
	}

}
