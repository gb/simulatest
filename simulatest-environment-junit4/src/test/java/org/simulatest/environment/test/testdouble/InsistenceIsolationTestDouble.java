package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

// Paired isolation scenario: the second test's precondition is that the first
// test's insert has been rolled back. Name-ascending method order keeps that
// pairing stable across JUnit 4 versions and filter orderings.
@UseEnvironment(InsistenceTestEnvironment.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InsistenceIsolationTestDouble {

	static { TestSetup.configure(); }

	@Test
	public void aInsertShouldBeVisibleWithinThisTest() throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("INSERT INTO test_isolation VALUES (100, 'test-specific')");

			try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation")) {
				rs.next();
				assertEquals("Should see environment data plus this test's insert", 2, rs.getInt(1));
			}
		}
	}

	@Test
	public void bNextTestShouldStartWithOnlyEnvironmentData() throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {

			try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation")) {
				rs.next();
				assertEquals("Previous test's insert should have been rolled back", 1, rs.getInt(1));
			}
		}
	}

}
