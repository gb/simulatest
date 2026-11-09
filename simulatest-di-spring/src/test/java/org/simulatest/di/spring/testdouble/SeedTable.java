package org.simulatest.di.spring.testdouble;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public final class SeedTable {

	private SeedTable() {}

	public static void create(Connection connection) throws SQLException {
		execute(connection, "CREATE TABLE seed (id INT)");
	}

	public static void drop(Connection connection) throws SQLException {
		execute(connection, "DROP TABLE seed");
	}

	public static void insertRow(Connection connection) throws SQLException {
		execute(connection, "INSERT INTO seed VALUES (1)");
	}

	public static int count(Connection connection) throws SQLException {
		try (Statement statement = connection.createStatement();
			 ResultSet rows = statement.executeQuery("SELECT COUNT(*) FROM seed")) {
			rows.next();
			return rows.getInt(1);
		}
	}

	private static void execute(Connection connection, String sql) throws SQLException {
		try (Statement statement = connection.createStatement()) {
			statement.execute(sql);
		}
	}

}
