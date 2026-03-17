package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Runner;
import org.junit.runners.JUnit4;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.junit.EnvironmentGrouperTests;

public class EnvironmentGrouperTestsTest {

	private EnvironmentGrouperTests grouperTests;

	@Before
	public void setup() {
		grouperTests = new EnvironmentGrouperTests(Set.of(EnvironmentGrouperTestsTest.class));
	}

	@Test
	public void getReturnsRunnerAfterPut() throws InitializationError {
		Runner runner = new JUnit4(EnvironmentGrouperTestsTest.class);
		grouperTests.put(EnvironmentGrouperTestsTest.class, runner);

		assertEquals(runner, grouperTests.get(EnvironmentGrouperTestsTest.class));
	}

	@Test(expected = IllegalStateException.class)
	public void getThrowsForUnknownTestClass() {
		grouperTests.get(String.class);
	}

}
