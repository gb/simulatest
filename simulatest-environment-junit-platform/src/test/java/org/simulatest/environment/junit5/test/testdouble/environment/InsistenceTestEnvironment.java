package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.Environment;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

public class InsistenceTestEnvironment implements Environment {

	public static void createTable(DataSource rawDataSource) {
		try (Connection connection = rawDataSource.getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS test_isolation (id INT PRIMARY KEY, name VARCHAR(100))");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to create test_isolation", e);
		}
	}

	@Override
	public void run() {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("INSERT INTO test_isolation VALUES (1, 'environment-data')");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to set up InsistenceTestEnvironment", e);
		}
	}

}
