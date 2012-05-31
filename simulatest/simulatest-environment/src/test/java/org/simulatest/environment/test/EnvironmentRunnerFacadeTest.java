package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.facade.EnvironmentRunnerFacade;
import org.simulatest.environment.test.testdouble.DatabaseMock;
import org.simulatest.environment.test.testdouble.environment.EnvironmentSecondLevel;

public class EnvironmentRunnerFacadeTest {
	
	private EnvironmentRunnerFacade environmentRunnerFacade;
	
	@Test
	public void shouldRunAllLevelsOfTreeWhenIRunAnEnvironment() {
		environmentRunnerFacade = new EnvironmentRunnerFacade();
		environmentRunnerFacade.runEnvironment(EnvironmentSecondLevel.class);
	
		assertEquals(2, DatabaseMock.getMessages().size());
		assertEquals("first", DatabaseMock.getMessages().get(0));
		assertEquals("second", DatabaseMock.getMessages().get(1));
	}
	
	@Test
	public void testEnvironmentFacadeWithChooseOfRunner() {
		environmentRunnerFacade = new EnvironmentRunnerFacade(new EnvironmentReflectionFactory());
		environmentRunnerFacade.runEnvironment(EnvironmentSecondLevel.class);
	
		assertEquals(2, DatabaseMock.getMessages().size());
		assertEquals("first", DatabaseMock.getMessages().get(0));
		assertEquals("second", DatabaseMock.getMessages().get(1));
	}

}
