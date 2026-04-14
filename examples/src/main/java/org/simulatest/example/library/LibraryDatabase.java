package org.simulatest.example.library;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Simple JDBC helpers for the Community Library example.
 *
 * All operations go through the Insistence Layer's wrapped DataSource, so every
 * INSERT, UPDATE, and DELETE is automatically managed by the savepoint stack.
 * No ORM, no connection pooling — just raw JDBC.
 */
public final class LibraryDatabase {

	private static final List<String> SCHEMA_DDL = List.of(
		"CREATE TABLE IF NOT EXISTS genre (" +
			"  id INT PRIMARY KEY," +
			"  name VARCHAR(50) NOT NULL" +
			")",
		"CREATE TABLE IF NOT EXISTS member_type (" +
			"  id INT PRIMARY KEY," +
			"  name VARCHAR(50) NOT NULL," +
			"  max_checkouts INT NOT NULL," +
			"  loan_period_days INT NOT NULL," +
			"  fine_per_day_cents INT NOT NULL" +
			")",
		"CREATE TABLE IF NOT EXISTS branch (" +
			"  id INT PRIMARY KEY," +
			"  name VARCHAR(100) NOT NULL," +
			"  address VARCHAR(200) NOT NULL" +
			")",
		"CREATE TABLE IF NOT EXISTS book (" +
			"  id INT PRIMARY KEY," +
			"  title VARCHAR(200) NOT NULL," +
			"  author VARCHAR(100) NOT NULL," +
			"  isbn VARCHAR(13)," +
			"  genre_id INT NOT NULL REFERENCES genre(id)," +
			"  publication_year INT" +
			")",
		"CREATE TABLE IF NOT EXISTS book_copy (" +
			"  id INT PRIMARY KEY," +
			"  book_id INT NOT NULL REFERENCES book(id)," +
			"  branch_id INT NOT NULL REFERENCES branch(id)," +
			"  status VARCHAR(20) NOT NULL DEFAULT 'AVAILABLE'" +
			")",
		"CREATE TABLE IF NOT EXISTS member (" +
			"  id INT PRIMARY KEY," +
			"  name VARCHAR(100) NOT NULL," +
			"  email VARCHAR(100) NOT NULL UNIQUE," +
			"  member_type_id INT NOT NULL REFERENCES member_type(id)," +
			"  home_branch_id INT NOT NULL REFERENCES branch(id)" +
			")",
		"CREATE TABLE IF NOT EXISTS staff (" +
			"  id INT PRIMARY KEY," +
			"  name VARCHAR(100) NOT NULL," +
			"  role VARCHAR(20) NOT NULL," +
			"  branch_id INT NOT NULL REFERENCES branch(id)" +
			")",
		"CREATE TABLE IF NOT EXISTS loan (" +
			"  id INT PRIMARY KEY," +
			"  book_copy_id INT NOT NULL REFERENCES book_copy(id)," +
			"  member_id INT NOT NULL REFERENCES member(id)," +
			"  checkout_date DATE NOT NULL," +
			"  due_date DATE NOT NULL," +
			"  return_date DATE" +
			")",
		"CREATE TABLE IF NOT EXISTS hold (" +
			"  id INT PRIMARY KEY," +
			"  book_id INT NOT NULL REFERENCES book(id)," +
			"  member_id INT NOT NULL REFERENCES member(id)," +
			"  hold_date DATE NOT NULL," +
			"  status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE'" +
			")"
	);

	private LibraryDatabase() {}

	public static Connection getConnection() throws SQLException {
		return InsistenceLayerFactory.requireDataSource().getConnection();
	}

	/**
	 * Creates the database schema. Must be called BEFORE the environment
	 * tree runs — DDL causes implicit commits that invalidate savepoints.
	 */
	public static void createSchema() {
		withStatement("Failed to create library schema", stmt -> {
			for (String ddl : SCHEMA_DDL) stmt.execute(ddl);
		});
	}

	public static void execute(String sql) {
		withStatement("Failed to execute statement", stmt -> stmt.execute(sql));
	}

	public static int queryInt(String sql) {
		return querySingleValue(sql, rs -> rs.getInt(1));
	}

	public static String queryString(String sql) {
		return querySingleValue(sql, rs -> rs.getString(1));
	}

	public static boolean queryExists(String sql) {
		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			return rs.next();
		} catch (SQLException e) {
			throw new LibraryDatabaseException("Query failed: " + sql, e);
		}
	}

	@FunctionalInterface
	private interface StatementAction {
		void run(Statement statement) throws SQLException;
	}

	@FunctionalInterface
	private interface ResultSetMapper<T> {
		T map(ResultSet rs) throws SQLException;
	}

	private static void withStatement(String failureMessage, StatementAction action) {
		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement()) {
			action.run(stmt);
		} catch (SQLException e) {
			throw new LibraryDatabaseException(failureMessage, e);
		}
	}

	private static <T> T querySingleValue(String sql, ResultSetMapper<T> mapper) {
		try (Connection conn = getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery(sql)) {
			if (!rs.next()) {
				throw new LibraryDatabaseException("Query returned no rows: " + sql);
			}
			return mapper.map(rs);
		} catch (SQLException e) {
			throw new LibraryDatabaseException("Query failed: " + sql, e);
		}
	}

}
