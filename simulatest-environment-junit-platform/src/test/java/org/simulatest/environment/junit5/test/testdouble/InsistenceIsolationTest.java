package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.InsistenceTestEnvironment;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

// Paired isolation scenario: the second test's precondition is that the first
// test's insert has been rolled back. Name-based ordering keeps that pairing
// stable across JUnit 5 configuration and filter orderings.
@UseEnvironment(InsistenceTestEnvironment.class)
@TestMethodOrder(MethodOrderer.MethodName.class)
public class InsistenceIsolationTest {

	@Test
	void aInsertShouldBeVisibleWithinThisTest() throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("INSERT INTO test_isolation VALUES (100, 'test-specific')");

			try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation")) {
				rs.next();
				assertEquals(2, rs.getInt(1),
						"Should see environment data plus this test's insert");
			}
		}
	}

	@Test
	void bNextTestShouldStartWithOnlyEnvironmentData() throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {

			try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation")) {
				rs.next();
				assertEquals(1, rs.getInt(1),
						"Previous test's insert should have been rolled back");
			}
		}
	}

}
