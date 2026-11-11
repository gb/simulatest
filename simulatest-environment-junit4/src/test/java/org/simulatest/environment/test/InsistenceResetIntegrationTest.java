package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.test.testdouble.ClassSetupSuite;
import org.simulatest.environment.test.testdouble.FailingClassSetupSuite;
import org.simulatest.environment.test.testdouble.FailingClassSetupTestDouble;
import org.simulatest.environment.test.testdouble.InsistenceIsolationSuite;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

public class InsistenceResetIntegrationTest {

	private static final List<String> NO_FAILURES = List.of();

	@Before
	public void createIsolationTable() throws Exception {
		TestSetup.configure();
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("DROP TABLE IF EXISTS test_isolation");
			stmt.execute("CREATE TABLE test_isolation (id INT PRIMARY KEY, name VARCHAR(100))");
		}
	}

	@Test
	public void insistenceLevelShouldResetBetweenTests() throws Exception {
		SuiteResult result = run(InsistenceIsolationSuite.class);

		assertEquals("The second test should not see the row of the first", NO_FAILURES, result.failures);
		assertEquals("Should have run 2 test methods", 2, result.testCount);
	}

	@Test
	public void classSetupDataShouldBeSeenByEveryTestOfTheClassAndNotByTheNextClass() throws Exception {
		SuiteResult result = run(ClassSetupSuite.class);

		assertEquals("Every test of the class should see the row its class added, and the next class should not",
				NO_FAILURES, result.failures);
		assertEquals("2 from ClassSetupTestDouble + 2 from InsistenceIsolationTestDouble", 4, result.testCount);
		assertEveryLevelIsClosed();
	}

	@Test
	public void dataOfAFailedClassSetupShouldNotReachTheNextClass() throws Exception {
		SuiteResult result = run(FailingClassSetupSuite.class);

		assertEquals("Only the class whose setup fails should be reported, and the next class should not see its row",
				List.of(FailingClassSetupTestDouble.class.getName()), result.failures);
		assertEquals("2 from InsistenceIsolationTestDouble", 2, result.testCount);
		assertEveryLevelIsClosed();
	}

	private static SuiteResult run(Class<?> suiteClass) throws Exception {
		SuiteResult result = new SuiteResult();
		RunNotifier notifier = new RunNotifier();
		notifier.addListener(result);

		new EnvironmentJUnitSuite(suiteClass, null).run(notifier);

		return result;
	}

	private static void assertEveryLevelIsClosed() {
		assertEquals("Every level should be closed after the run",
				0, InsistenceLayerFactory.resolve().orElseThrow().getCurrentLevel());
	}

	private static final class SuiteResult extends RunListener {

		private final List<String> failures = new ArrayList<>();
		private int testCount;

		@Override
		public void testFinished(Description description) {
			testCount++;
		}

		@Override
		public void testFailure(Failure failure) {
			failures.add(failure.getDescription().getDisplayName());
		}

	}

}
