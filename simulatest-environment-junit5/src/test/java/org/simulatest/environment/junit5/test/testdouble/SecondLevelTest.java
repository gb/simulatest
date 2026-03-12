package org.simulatest.environment.junit5.test.testdouble;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.SecondLevelEnvironment;

@UseEnvironment(SecondLevelEnvironment.class)
public class SecondLevelTest {

	@Test
	public void shouldHaveBothEnvironments() {
		Assertions.assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"));
		Assertions.assertTrue(EnvironmentTracker.getEvents().contains("SecondLevel"));
	}

}
