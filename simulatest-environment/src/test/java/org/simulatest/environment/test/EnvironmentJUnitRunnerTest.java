package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.environment.test.testdouble.DummyTest;

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
		
		String dummyTestQualifiedName = DummyTest.class.getName();
		
		son.addChild(Description.createSuiteDescription("testSum(" + dummyTestQualifiedName + ")"));
		son.addChild(Description.createSuiteDescription("testSubtract(" + dummyTestQualifiedName + ")"));
		son.addChild(Description.createSuiteDescription("testMultiply(" + dummyTestQualifiedName + ")"));
		son.addChild(Description.createSuiteDescription("testDivision(" + dummyTestQualifiedName + ")"));
		
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