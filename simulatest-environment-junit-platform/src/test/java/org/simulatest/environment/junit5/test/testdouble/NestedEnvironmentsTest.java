package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.SecondLevelEnvironment;

/**
 * Test double: outer class has no {@code @UseEnvironment}. Each {@code @Nested}
 * inner class declares its own environment. Used to verify that the Simulatest
 * engine discovers {@code @UseEnvironment} on inner classes.
 */
public class NestedEnvironmentsTest {

	@Nested
	@UseEnvironment(FirstLevelEnvironment.class)
	class AtFirstLevel {
		@Test
		void shouldSeeFirstLevelEnvironment() {
			assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"));
		}
	}

	@Nested
	@UseEnvironment(SecondLevelEnvironment.class)
	class AtSecondLevel {
		@Test
		void shouldSeeSecondLevelEnvironment() {
			assertTrue(EnvironmentTracker.getEvents().contains("SecondLevel"));
		}

		@Test
		void shouldAlsoSeeFirstLevelFromParentEnvironment() {
			assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"));
		}
	}
}
