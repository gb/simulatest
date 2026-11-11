package org.simulatest.environment.test.testdouble;

import static org.junit.Assert.assertEquals;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.test.TestSetup;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

// The class adds one row of its own before its tests. Every test should see it,
// next to the environment row, and not the row of the test that ran before.
@UseEnvironment(InsistenceTestEnvironment.class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class ClassSetupTestDouble {

	static { TestSetup.configure(); }

	@BeforeClass
	public static void addClassRow() throws SQLException {
		execute("INSERT INTO test_isolation VALUES (50, 'class-data')");
	}

	@Test
	public void aShouldSeeTheEnvironmentRowAndTheClassRow() throws SQLException {
		assertEquals(2, countRows());
		execute("INSERT INTO test_isolation VALUES (100, 'test-specific')");
	}

	@Test
	public void bShouldSeeTheEnvironmentRowAndTheClassRow() throws SQLException {
		assertEquals(2, countRows());
		execute("INSERT INTO test_isolation VALUES (100, 'test-specific')");
	}

	private static void execute(String sql) throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute(sql);
		}
	}

	private static int countRows() throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM test_isolation")) {
			rs.next();
			return rs.getInt(1);
		}
	}

}
