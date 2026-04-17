package org.simulatest.environment.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.sql.Connection;
import java.sql.Statement;

import org.junit.Test;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.test.testdouble.InsistenceIsolationSuite;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

public class InsistenceResetIntegrationTest {

	@Test
	public void insistenceLevelShouldResetBetweenTests() throws Exception {
		TestSetup.configure();
		createIsolationTable();

		EnvironmentJUnitSuite suite = new EnvironmentJUnitSuite(InsistenceIsolationSuite.class, null);

		RunNotifier notifier = new RunNotifier();
		FailureCollector collector = new FailureCollector();
		notifier.addListener(collector);

		suite.run(notifier);

		assertNull("Insistence reset between tests should ensure data isolation, but got failure: "
				+ (collector.failure != null ? collector.failure.getMessage() : ""),
				collector.failure);
		assertEquals("Should have run 2 test methods", 2, collector.testCount);
	}

	private void createIsolationTable() throws Exception {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("DROP TABLE IF EXISTS test_isolation");
			stmt.execute("CREATE TABLE test_isolation (id INT PRIMARY KEY, name VARCHAR(100))");
		}
	}

	private static class FailureCollector extends RunListener {
		Failure failure;
		int testCount;

		@Override
		public void testFinished(org.junit.runner.Description description) {
			testCount++;
		}

		@Override
		public void testFailure(Failure failure) {
			if (this.failure == null) this.failure = failure;
		}
	}

}
