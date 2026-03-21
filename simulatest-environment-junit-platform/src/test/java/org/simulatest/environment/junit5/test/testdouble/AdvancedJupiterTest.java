package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.stream.Stream;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

@UseEnvironment(FirstLevelEnvironment.class)
public class AdvancedJupiterTest {

	@ParameterizedTest
	@ValueSource(strings = {"alpha", "beta", "gamma"})
	void parameterizedTestShouldSeeEnvironment(String value) {
		assertNotNull(value);
		assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"),
				"Environment should be set up for parameterized test with value: " + value);
	}

	@RepeatedTest(3)
	void repeatedTestShouldSeeEnvironment() {
		assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"),
				"Environment should be set up for each repetition");
	}

	@TestFactory
	Stream<DynamicTest> dynamicTestsShouldSeeEnvironment() {
		return Stream.of("one", "two").map(name ->
				DynamicTest.dynamicTest("dynamic-" + name, () ->
						assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"),
								"Environment should be set up for dynamic test: " + name)));
	}

	@Nested
	class InnerTest {
		@Test
		void nestedTestShouldSeeEnvironment() {
			assertTrue(EnvironmentTracker.getEvents().contains("FirstLevel"),
					"Environment should be set up for nested test");
		}
	}

}
