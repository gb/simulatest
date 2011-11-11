package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.environment.facade.EnvironmentRunnerFacade;
import org.simulatest.environment.test.testdouble.DatabaseMock;
import org.simulatest.environment.test.testdouble.environment.EnvironmentSecondLevel;

public class EnvironmentRunnerFacadeTest {
	
	private EnvironmentRunnerFacade environmentRunnerFacade = new EnvironmentRunnerFacade();
	
	@Test
	public void shouldRunAllLevelsOfTreeWhenIRunAnEnvironment() {
		environmentRunnerFacade.runEnvironment(EnvironmentSecondLevel.class);
	
		assertEquals(2, DatabaseMock.getMessages().size());
		assertEquals("first", DatabaseMock.getMessages().get(0));
		assertEquals("second", DatabaseMock.getMessages().get(1));
	}
	

}
