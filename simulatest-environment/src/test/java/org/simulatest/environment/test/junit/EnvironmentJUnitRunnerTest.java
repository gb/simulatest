package org.simulatest.environment.test.junit;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.environment.mock.DummyTest;

public class EnvironmentJUnitRunnerTest {
	
	private EnvironmentJUnitRunner runner;
	
	@Before
	public void setup() throws InitializationError {
		runner = new EnvironmentJUnitRunner(DummyTest.class);
	}
	
	@Test
	public void testDescriptions() {
		Description root = Description.createSuiteDescription(BigBangEnvironment.class.getName());
		Description son = Description.createSuiteDescription(DummyTest.class.getName());
		
		root.addChild(son);
		
		son.addChild(Description.createSuiteDescription("testSum(org.simulatest.environment.mock.DummyTest)"));
		son.addChild(Description.createSuiteDescription("testSubtract(org.simulatest.environment.mock.DummyTest)"));
		son.addChild(Description.createSuiteDescription("testMultiply(org.simulatest.environment.mock.DummyTest)"));
		son.addChild(Description.createSuiteDescription("testDivision(org.simulatest.environment.mock.DummyTest)"));
		
		/*
		 * -BigBang
		 *     -DummyTest
		 *         -testSum
		 *         -testSubtract
		 *         -testMultiply
		 *         -testDivision
		 */
		
		assertEquals(root, runner.getDescription());
	}

}