package org.simulatest.di.spring;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.simulatest.di.spring.testdouble.SeedDataSourceConfig;
import org.simulatest.di.spring.testdouble.SeedSuiteTest;
import org.simulatest.di.spring.testdouble.SeedTable;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Runs the same JUnit 4 test class twice in one JVM, each run with its own
 * {@link EnvironmentJUnitRunner} and its own Spring context that declares a
 * DataSource bean, as Surefire does for every JUnit 4 class. The rows the
 * environment writes must roll back in the second run like in the first.
 * The count goes through a separate connection, so it sees only rows that
 * reached the database.
 */
public class SpringDataSourceAcrossSessionsTest {

	private Connection observer;

	@Before
	public void createSchema() throws SQLException {
		InsistenceLayerFactory.clear();
		observer = DriverManager.getConnection(SeedDataSourceConfig.URL);
		SeedTable.create(observer);
	}

	@After
	public void dropSchema() throws SQLException {
		try {
			SeedTable.drop(observer);
		} finally {
			observer.close();
		}
	}

	@Test
	public void secondTestClassInSameJvmMustNotPersistItsEnvironmentRows() throws SQLException {
		runAndExpectSuccess();
		assertEquals("rows of the first class must be rolled back", 0, SeedTable.count(observer));

		runAndExpectSuccess();
		assertEquals("rows of the second class must be rolled back", 0, SeedTable.count(observer));
	}

	private static void runAndExpectSuccess() {
		Result result = JUnitCore.runClasses(SeedSuiteTest.class);
		assertTrue(String.valueOf(result.getFailures()), result.wasSuccessful());
	}

}
