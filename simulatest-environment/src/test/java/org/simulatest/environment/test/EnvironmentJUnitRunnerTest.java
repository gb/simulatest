package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.manipulation.Filter;
import org.junit.runner.manipulation.NoTestsRemainException;
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

	@Test(expected = NoTestsRemainException.class)
	public void filterShouldThrowWhenNoTestsRemain() throws NoTestsRemainException {
		runner.filter(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				return false;
			}

			@Override
			public String describe() {
				return "exclude all";
			}
		});
	}

	@Test
	public void filterShouldKeepRunnerWhenAtLeastOneTestRemains() throws NoTestsRemainException {
		runner.filter(new Filter() {
			@Override
			public boolean shouldRun(Description description) {
				return description.getMethodName() == null || "testSum".equals(description.getMethodName());
			}

			@Override
			public String describe() {
				return "keep only testSum";
			}
		});

		Description filtered = runner.getDescription();
		assertEquals(1, filtered.getChildren().size());
		assertEquals(1, filtered.getChildren().get(0).getChildren().size());
		assertEquals("testSum", filtered.getChildren().get(0).getChildren().get(0).getMethodName());
	}

}
