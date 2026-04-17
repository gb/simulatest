package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.EnvironmentRunner;
import org.simulatest.environment.test.testdouble.DatabaseMock;
import org.simulatest.environment.test.testdouble.environment.EnvironmentSecondLevel;

public class EnvironmentRunnerFacadeTest {

	@Before
	public void resetDatabaseMock() {
		DatabaseMock.reset();
	}

	@Test
	public void shouldRunAllLevelsOfTreeWhenIRunAnEnvironment() {
		EnvironmentRunner.runEnvironment(EnvironmentSecondLevel.class);

		assertEquals(2, DatabaseMock.getMessages().size());
		assertEquals("first", DatabaseMock.getMessages().get(0));
		assertEquals("second", DatabaseMock.getMessages().get(1));
	}

	@Test
	public void shouldRunAllLevelsOfTreeWhenUsingSuppliedFactory() {
		EnvironmentRunner.runEnvironment(EnvironmentSecondLevel.class, new EnvironmentReflectionFactory());

		assertEquals(2, DatabaseMock.getMessages().size());
		assertEquals("first", DatabaseMock.getMessages().get(0));
		assertEquals("second", DatabaseMock.getMessages().get(1));
	}

}
