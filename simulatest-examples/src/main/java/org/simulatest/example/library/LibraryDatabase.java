package org.simulatest.example.library;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

/**
 * Simple JDBC helpers for the Community Library example.
 *
 * All operations go through the {@link InsistenceLayerDataSource}, so every
 * INSERT, UPDATE, and DELETE is automatically managed by the Insistence
 * Layer's savepoint stack. No ORM, no connection pooling — just raw JDBC.
 */
public class LibraryDatabase {

	public static Connection getConnection() throws SQLException {
		return InsistenceLayerDataSource.getDefault().getConnection();
	}

	/**
	 * Creates the database schema. Must be called BEFORE the environment
	 * tree runs — DDL causes implicit commits that invalidate savepoints.
	 */
	public static void createSchema() {
		try {
			Connection conn = getConnection();
			try (Statement stmt = conn.createStatement()) {
				stmt.execute(
					"CREATE TABLE IF NOT EXISTS genre (" +
					"  id INT PRIMARY KEY," +
					"  name VARCHAR(50) NOT NULL" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS member_type (" +
					"  id INT PRIMARY KEY," +
					"  name VARCHAR(50) NOT NULL," +
					"  max_checkouts INT NOT NULL," +
					"  loan_period_days INT NOT NULL," +
					"  fine_per_day_cents INT NOT NULL" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS branch (" +
					"  id INT PRIMARY KEY," +
					"  name VARCHAR(100) NOT NULL," +
					"  address VARCHAR(200) NOT NULL" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS book (" +
					"  id INT PRIMARY KEY," +
					"  title VARCHAR(200) NOT NULL," +
					"  author VARCHAR(100) NOT NULL," +
					"  isbn VARCHAR(13)," +
					"  genre_id INT NOT NULL REFERENCES genre(id)," +
					"  publication_year INT" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS book_copy (" +
					"  id INT PRIMARY KEY," +
					"  book_id INT NOT NULL REFERENCES book(id)," +
					"  branch_id INT NOT NULL REFERENCES branch(id)," +
					"  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS member (" +
					"  id INT PRIMARY KEY," +
					"  name VARCHAR(100) NOT NULL," +
					"  email VARCHAR(100) NOT NULL UNIQUE," +
					"  member_type_id INT NOT NULL REFERENCES member_type(id)," +
					"  home_branch_id INT NOT NULL REFERENCES branch(id)" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS staff (" +
					"  id INT PRIMARY KEY," +
					"  name VARCHAR(100) NOT NULL," +
					"  role VARCHAR(20) NOT NULL," +
					"  branch_id INT NOT NULL REFERENCES branch(id)" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS loan (" +
					"  id INT PRIMARY KEY," +
					"  book_copy_id INT NOT NULL REFERENCES book_copy(id)," +
					"  member_id INT NOT NULL REFERENCES member(id)," +
					"  checkout_date DATE NOT NULL," +
					"  due_date DATE NOT NULL," +
					"  return_date DATE" +
					")"
				);

				stmt.execute(
					"CREATE TABLE IF NOT EXISTS hold (" +
					"  id INT PRIMARY KEY," +
					"  book_id INT NOT NULL REFERENCES book(id)," +
					"  member_id INT NOT NULL REFERENCES member(id)," +
					"  hold_date DATE NOT NULL," +
					"  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'" +
					")"
				);
			}
		} catch (SQLException e) {
			throw new RuntimeException("Failed to create library schema", e);
		}
	}

	/** Executes a SQL statement (INSERT, UPDATE, DELETE). */
	public static void execute(String sql) {
		try (Statement stmt = getConnection().createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public static int queryInt(String sql) {
		return querySingleValue(sql, rs -> rs.getInt(1));
	}

	public static String queryString(String sql) {
		return querySingleValue(sql, rs -> rs.getString(1));
	}

	public static boolean queryExists(String sql) {
		try (Statement stmt = getConnection().createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			return rs.next();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	// --- Private helpers ---

	@FunctionalInterface
	private interface ResultSetMapper<T> {
		T map(ResultSet rs) throws SQLException;
	}

	private static <T> T querySingleValue(String sql, ResultSetMapper<T> mapper) {
		try (Statement stmt = getConnection().createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			if (!rs.next()) {
				throw new RuntimeException("Query returned no rows: " + sql);
			}
			return mapper.map(rs);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

}
