package org.simulatest.environment.test.testdouble;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Rows of the test_isolation table, written and counted by the class setup
 * test doubles through the Insistence Layer connection.
 */
final class IsolationRows {

	private IsolationRows() { }

	static void insert(int id, String name) throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute("INSERT INTO test_isolation VALUES (" + id + ", '" + name + "')");
		}
	}

	static int count() throws SQLException {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM test_isolation")) {
			resultSet.next();
			return resultSet.getInt(1);
		}
	}

}
