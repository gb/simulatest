package org.simulatest.environment.junit5.test.testdouble.environment;

import org.simulatest.environment.Environment;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class InsistenceTestEnvironment implements Environment {

	@Override
	public void run() {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement stmt = connection.createStatement()) {
			stmt.execute("CREATE TABLE IF NOT EXISTS test_isolation (id INT PRIMARY KEY, name VARCHAR(100))");
			stmt.execute("DELETE FROM test_isolation");
			stmt.execute("INSERT INTO test_isolation VALUES (1, 'environment-data')");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to set up InsistenceTestEnvironment", e);
		}
	}

}
