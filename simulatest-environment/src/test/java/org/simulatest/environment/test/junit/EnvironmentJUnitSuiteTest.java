package org.simulatest.environment.test.junit;

import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;
import org.junit.internal.builders.AllDefaultPossibilitiesBuilder;
import org.junit.runner.Description;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.mock.AnotherDummyTest;
import org.simulatest.environment.mock.DummyTest;
import org.simulatest.environment.mock.SuiteTest;

public class EnvironmentJUnitSuiteTest {
	
	private EnvironmentJUnitSuite suite;
	
	@Before
	public void setup() throws InitializationError {
		suite = new EnvironmentJUnitSuite(SuiteTest.class, new AllDefaultPossibilitiesBuilder(true));
	}
	
	@Test
	public void testDescriptions() {
		Description dummyTest = Description.createSuiteDescription(DummyTest.class.getName());
		
		dummyTest.addChild(Description.createSuiteDescription("testSum(org.simulatest.environment.mock.DummyTest)"));
		dummyTest.addChild(Description.createSuiteDescription("testSubtract(org.simulatest.environment.mock.DummyTest)"));
		dummyTest.addChild(Description.createSuiteDescription("testMultiply(org.simulatest.environment.mock.DummyTest)"));
		dummyTest.addChild(Description.createSuiteDescription("testDivision(org.simulatest.environment.mock.DummyTest)"));
		
		Description anotherDummyTest = Description.createSuiteDescription(AnotherDummyTest.class.getName());
		anotherDummyTest.addChild(Description.createSuiteDescription("testTrue(org.simulatest.environment.mock.AnotherDummyTest)"));
		anotherDummyTest.addChild(Description.createSuiteDescription("testFalse(org.simulatest.environment.mock.AnotherDummyTest)"));
		
		/*
		 * -BigBang
		 *     -DummyTest
		 *         -testSum
		 *         -testSubtract
		 *         -testMultiply
		 *         -testDivision
		 *     -AnotherDummyTest
		 *     	   -testTrue
		 *     	   -testFalse
		 */
		
		assertTrue(suite.getDescription().getDisplayName().equals(BigBangEnvironment.class.getName()));
		assertTrue(suite.getDescription().getChildren().contains(dummyTest));
		assertTrue(suite.getDescription().getChildren().contains(anotherDummyTest));
	}

}