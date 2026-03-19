package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

@UseEnvironment(InsistenceTestEnvironment.class)
public class InsistenceIsolationTestDouble {

	static { TestSetup.configure(); }

	@Test
	public void insertShouldBeVisibleWithinThisTest() throws SQLException {
		Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
		Statement stmt = connection.createStatement();
		stmt.execute("INSERT INTO test_isolation VALUES (100, 'test-specific')");

		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation");
		rs.next();
		assertEquals("Should see environment data plus this test's insert", 2, rs.getInt(1));
	}

	@Test
	public void eachTestShouldStartWithOnlyEnvironmentData() throws SQLException {
		Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
		Statement stmt = connection.createStatement();

		ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation");
		rs.next();
		assertEquals("Previous test's insert should have been rolled back", 1, rs.getInt(1));
	}

}
