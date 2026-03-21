package org.simulatest.environment.junit5.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.InsistenceTestEnvironment;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

@UseEnvironment(InsistenceTestEnvironment.class)
public class InsistenceIsolationTest {

	@Test
	void insertShouldBeVisibleWithinThisTest() throws SQLException {
		Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.execute("INSERT INTO test_isolation VALUES (100, 'test-specific')");

		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation");
		rs.next();
		assertEquals(2, rs.getInt(1),
				"Should see environment data plus this test's insert");
	}

	@Test
	void eachTestShouldStartWithOnlyEnvironmentData() throws SQLException {
		Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
		Statement stmt = connection.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation");
		rs.next();
		assertEquals(1, rs.getInt(1),
				"Previous test's insert should have been rolled back");
	}

}
