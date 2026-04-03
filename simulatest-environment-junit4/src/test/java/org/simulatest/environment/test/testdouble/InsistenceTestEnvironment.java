package org.simulatest.environment.test.testdouble;

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
			stmt.execute("INSERT INTO test_isolation VALUES (1, 'environment-data')");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to set up InsistenceTestEnvironment", e);
		}
	}

}
