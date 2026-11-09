package org.simulatest.environment.junit5.test.testdouble;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Rows written by the row environments, read back by their tests.
 */
public final class EnvRows {

	private EnvRows() { }

	public static void createTable(DataSource rawDataSource) {
		try (Connection connection = rawDataSource.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute("CREATE TABLE IF NOT EXISTS env_rows (id INT PRIMARY KEY, name VARCHAR(100))");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to create env_rows", e);
		}
	}

	public static void insert(int id, String name) {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement statement = connection.createStatement()) {
			statement.execute("INSERT INTO env_rows VALUES (" + id + ", '" + name + "')");
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to insert into env_rows", e);
		}
	}

	public static int count() {
		try (Connection connection = InsistenceLayerFactory.requireDataSource().getConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery("SELECT COUNT(*) FROM env_rows")) {
			resultSet.next();
			return resultSet.getInt(1);
		} catch (SQLException e) {
			throw new EnvironmentExecutionException("Failed to count env_rows", e);
		}
	}

}
