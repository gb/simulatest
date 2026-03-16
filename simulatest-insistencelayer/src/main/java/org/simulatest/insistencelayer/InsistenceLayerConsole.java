package org.simulatest.insistencelayer;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Interactive SQL console for debugging test database state.
 *
 * <p>The Insistence Layer isolates test data inside savepoints on a single
 * connection. This means external database clients cannot see the data.
 * This console runs on that same connection, so it sees exactly what the
 * test sees.</p>
 *
 * <h3>When to use</h3>
 * <ul>
 *   <li>A test fails and you need to inspect what data exists at a specific point</li>
 *   <li>You want to understand what state an environment sets up</li>
 *   <li>You need to verify SQL statements produce the expected rows</li>
 *   <li>You are an AI agent debugging a test failure and need to see the database</li>
 * </ul>
 *
 * <h3>Usage</h3>
 * <p>Add {@code InsistenceLayerConsole.debug()} at the point in the test where
 * you want to pause and inspect:</p>
 * <pre>
 * {@literal @}Test
 * public void myTest() throws Exception {
 *     // ... setup ...
 *     InsistenceLayerConsole.debug(); // pauses here
 *     // ... assertions ...
 * }
 * </pre>
 *
 * <p>Run the test from the terminal:</p>
 * <pre>
 * mvn -pl module -Dtest=TestClass#method test
 * </pre>
 *
 * <p>The console will display available tables, accept SQL queries, and
 * type {@code resume} to continue test execution. Remove the debug() call
 * when done.</p>
 *
 * <h3>Available commands</h3>
 * <ul>
 *   <li>Any SQL statement (SELECT, INSERT, UPDATE, DELETE)</li>
 *   <li>{@code tables} - list all tables</li>
 *   <li>{@code schema <table>} - show column definitions</li>
 *   <li>{@code resume} - continue test execution</li>
 * </ul>
 *
 * <h3>For IDE usage</h3>
 * <p>IDE test runners do not support terminal input. Use
 * {@code SimulatestSQLWindow.debug()} from the simulatest-gui module instead,
 * which opens a Swing window on the same connection.</p>
 *
 * @see InsistenceLayer
 * @see InsistenceLayerFactory
 */
public class InsistenceLayerConsole {

	private static final Logger logger = LoggerFactory.getLogger(InsistenceLayerConsole.class);
	private static final String DEBUG_NOTICE =
		"This console was triggered by an InsistenceLayerDebugger.debug() call\n" +
		"in your test. If this was unintentional, remove that line.";

	public static void debug() {
		var dataSource = InsistenceLayerFactory.dataSource();
		if (dataSource == null) {
			logger.warn("No InsistenceLayer DataSource configured, cannot open console");
			return;
		}

		logger.info("Insistence Layer Console waiting for input. Type SQL in the console, 'resume' to continue test.");

		try {
			Connection connection = dataSource.getConnection();
			run(connection, System.in != null ? new BufferedReader(new InputStreamReader(System.in)) : null, System.out);
		} catch (SQLException e) {
			logger.error("Failed to obtain connection for console", e);
		}
	}

	static void run(Connection connection, BufferedReader reader, PrintStream out) {
		if (reader == null) {
			logger.warn("No input stream available, cannot open console");
			return;
		}

		if (!isStdinConnected(reader)) {
			out.println();
			out.println("=== Insistence Layer Debug Console ===");
			out.println("ERROR: stdin is not connected. Maven Surefire forks a new JVM that");
			out.println("does not receive terminal input. Re-run with -DforkCount=0:");
			out.println();
			out.println("  mvn -Dtest=YourTest -DforkCount=0 test");
			out.println();
			out.println("This console was triggered by an InsistenceLayerDebugger.debug() call");
			out.println("in your test. If this was unintentional, remove that line.");
			out.println();
			out.flush();
			return;
		}

		InsistenceLayer layer = InsistenceLayerFactory.resolve();
		int level = layer != null ? layer.getCurrentLevel() : -1;

		out.println();
		out.println("=== Insistence Layer Debug Console ===");
		out.println("Test execution is paused. You are connected to the test database");
		out.println("through the Insistence Layer (savepoint level: " + level + ").");
		out.println("All data visible here is isolated to this connection.");
		out.println();
		out.println("Available commands:");
		out.println("  <SQL>            Execute any SQL query (SELECT, INSERT, UPDATE, DELETE)");
		out.println("  tables           List all tables in the database");
		out.println("  schema <table>   Show column definitions for a table");
		out.println("  resume           Continue test execution");
		out.println();
		out.println(DEBUG_NOTICE);
		out.println();

		printTables(connection, out);
		out.flush();

		try (Statement statement = connection.createStatement()) {
			while (true) {
				out.print("sql> ");
				out.flush();

				String line = reader.readLine();
				if (line == null) break;

				String input = line.trim();
				if (input.isEmpty()) continue;

				if ("resume".equalsIgnoreCase(input)) {
					out.println("=== Resuming test execution ===");
					break;
				}

				if ("tables".equalsIgnoreCase(input)) {
					printTables(connection, out);
					continue;
				}

				if (input.toLowerCase().startsWith("schema ")) {
					String table = input.substring(7).trim();
					printSchema(connection, table, out);
					continue;
				}

				try {
					if (statement.execute(input)) {
						printResultSet(statement.getResultSet(), out);
					} else {
						out.println("Updated " + statement.getUpdateCount() + " row(s)");
						out.println();
					}
				} catch (SQLException e) {
					out.println("ERROR: " + e.getMessage());
					out.println();
				}
			}
		} catch (Exception e) {
			logger.error("Console error", e);
		}
	}

	private static boolean isStdinConnected(BufferedReader reader) {
		if (System.console() != null) return true;

		try {
			return reader.ready();
		} catch (Exception e) {
			return false;
		}
	}

	private static void printTables(Connection connection, PrintStream out) {
		try {
			DatabaseMetaData meta = connection.getMetaData();

			List<String> tables = new ArrayList<>();
			try (ResultSet rs = meta.getTables(null, null, "%", new String[]{"TABLE"})) {
				while (rs.next()) {
					String schema = rs.getString("TABLE_SCHEM");
					if ("INFORMATION_SCHEMA".equalsIgnoreCase(schema)) continue;
					tables.add(rs.getString("TABLE_NAME"));
				}
			}

			if (tables.isEmpty()) {
				out.println("Tables: (none)");
			} else {
				out.println("Tables: " + String.join(", ", tables));
			}
			out.println();
		} catch (SQLException e) {
			out.println("Could not list tables: " + e.getMessage());
			out.println();
		}
	}

	private static void printSchema(Connection connection, String table, PrintStream out) {
		try {
			DatabaseMetaData meta = connection.getMetaData();

			List<String[]> columns = new ArrayList<>();
			int nameWidth = 6, typeWidth = 4, nullWidth = 8;

			try (ResultSet rs = meta.getColumns(null, null, table.toUpperCase(), "%")) {
				while (rs.next()) {
					String name = rs.getString("COLUMN_NAME");
					String type = rs.getString("TYPE_NAME");
					int size = rs.getInt("COLUMN_SIZE");
					String nullable = "YES".equals(rs.getString("IS_NULLABLE")) ? "nullable" : "not null";

					String typeStr = type + "(" + size + ")";
					columns.add(new String[]{name, typeStr, nullable});

					nameWidth = Math.max(nameWidth, name.length());
					typeWidth = Math.max(typeWidth, typeStr.length());
					nullWidth = Math.max(nullWidth, nullable.length());
				}
			}

			if (columns.isEmpty()) {
				out.println("Table not found: " + table);
			} else {
				out.println("Schema: " + table.toUpperCase());
				int[] widths = {nameWidth, typeWidth, nullWidth};
				printRow(new String[]{"COLUMN", "TYPE", "NULLABLE"}, widths, out);
				printSeparator(widths, out);
				for (String[] col : columns) {
					printRow(col, widths, out);
				}
			}
			out.println();
		} catch (SQLException e) {
			out.println("Could not read schema: " + e.getMessage());
			out.println();
		}
	}

	private static void printResultSet(ResultSet rs, PrintStream out) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();

		String[] headers = new String[colCount];
		int[] widths = new int[colCount];

		for (int i = 0; i < colCount; i++) {
			headers[i] = meta.getColumnName(i + 1);
			widths[i] = headers[i].length();
		}

		List<String[]> rows = new ArrayList<>();
		while (rs.next()) {
			String[] row = new String[colCount];
			for (int i = 0; i < colCount; i++) {
				String value = rs.getString(i + 1);
				row[i] = value != null ? value : "NULL";
				widths[i] = Math.max(widths[i], row[i].length());
			}
			rows.add(row);
		}

		printRow(headers, widths, out);
		printSeparator(widths, out);
		for (String[] row : rows) {
			printRow(row, widths, out);
		}

		out.println("(" + rows.size() + " row" + (rows.size() != 1 ? "s" : "") + ")");
		out.println();
	}

	private static void printRow(String[] values, int[] widths, PrintStream out) {
		StringBuilder sb = new StringBuilder("| ");
		for (int i = 0; i < values.length; i++) {
			if (i > 0) sb.append(" | ");
			sb.append(String.format("%-" + widths[i] + "s", values[i]));
		}
		sb.append(" |");
		out.println(sb);
	}

	private static void printSeparator(int[] widths, PrintStream out) {
		StringBuilder sb = new StringBuilder("|");
		for (int i = 0; i < widths.length; i++) {
			sb.append("-");
			sb.append("-".repeat(widths[i]));
			sb.append("-|");
		}
		out.println(sb);
	}

}
