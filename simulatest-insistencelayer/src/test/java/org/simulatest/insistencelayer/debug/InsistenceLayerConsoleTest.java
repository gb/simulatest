package org.simulatest.insistencelayer.debug;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.sql.Connection;
import java.sql.Statement;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.util.TestDataSources;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

public class InsistenceLayerConsoleTest {

	private InsistenceLayer insistenceLayer;
	private Connection connection;
	private ByteArrayOutputStream outputBuffer;
	private PrintStream out;

	@Before
	public void setup() throws Exception {
		InsistenceLayerDataSource ds = new InsistenceLayerDataSource(TestDataSources.createH2("consoletest"));
		connection = ds.getConnection();
		insistenceLayer = InsistenceLayerFactory.build(ds.getConnectionWrapper());
		InsistenceLayerFactory.register(InsistenceLayerFactory.DEFAULT, insistenceLayer);

		Statement stmt = connection.createStatement();
		stmt.executeUpdate("CREATE TABLE IF NOT EXISTS USERS (ID INT, NAME VARCHAR(50))");
		stmt.close();

		insistenceLayer.increaseLevel();

		outputBuffer = new ByteArrayOutputStream();
		out = new PrintStream(outputBuffer);
	}

	@After
	public void teardown() {
		insistenceLayer.decreaseAllLevels();
		InsistenceLayerFactory.clear();
	}

	@Test
	public void bannerShowsContextAndInstructions() throws Exception {
		BufferedReader reader = readerWith("resume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue("banner should show current savepoint level. Output: " + output,
				output.contains("savepoint level: 1"));
		assertTrue("banner should explain connection isolation. Output: " + output,
				output.contains("isolated to this connection"));
		assertTrue("banner should list the USERS table. Output: " + output,
				output.contains("USERS"));
		assertTrue("banner should mention the resume command. Output: " + output,
				output.contains("resume"));
		assertTrue("banner should mention the schema command. Output: " + output,
				output.contains("schema <table>"));
	}

	@Test
	public void selectQueryDisplaysResults() throws Exception {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("INSERT INTO USERS VALUES (1, 'Alice')");
		stmt.executeUpdate("INSERT INTO USERS VALUES (2, 'Bob')");
		stmt.close();

		BufferedReader reader = readerWith("SELECT * FROM USERS ORDER BY ID\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("Alice"));
		assertTrue(output.contains("Bob"));
		assertTrue(output.contains("(2 rows)"));
	}

	@Test
	public void updateQueryDisplaysRowCount() throws Exception {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("INSERT INTO USERS VALUES (1, 'Alice')");
		stmt.close();

		BufferedReader reader = readerWith("UPDATE USERS SET NAME = 'Carol' WHERE ID = 1\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("Updated 1 row(s)"));
	}

	@Test
	public void invalidSqlDisplaysError() throws Exception {
		BufferedReader reader = readerWith("NOT VALID SQL\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("ERROR:"));
	}

	@Test
	public void displaysCurrentLevel() throws Exception {
		insistenceLayer.increaseLevel();
		assertEquals(2, insistenceLayer.getCurrentLevel());

		BufferedReader reader = readerWith("resume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("savepoint level: 2"));
	}

	@Test
	public void resumeEndsSession() throws Exception {
		BufferedReader reader = readerWith("\n\n\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("Resuming test execution"));
	}

	@Test
	public void nullReaderIsHandledGracefully() {
		InsistenceLayerConsole.run(connection, null, out);
		// should not throw
	}

	@Test
	public void nullValueDisplaysAsNULL() throws Exception {
		Statement stmt = connection.createStatement();
		stmt.executeUpdate("INSERT INTO USERS VALUES (1, NULL)");
		stmt.close();

		BufferedReader reader = readerWith("SELECT * FROM USERS\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("NULL"));
	}

	@Test
	public void tablesCommandListsTables() throws Exception {
		BufferedReader reader = readerWith("tables\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		// USERS appears twice: once in banner, once from command
		int firstIndex = output.indexOf("USERS");
		int secondIndex = output.indexOf("USERS", firstIndex + 1);
		assertTrue(secondIndex > firstIndex);
	}

	@Test
	public void schemaCommandShowsColumns() throws Exception {
		BufferedReader reader = readerWith("schema users\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("Schema: USERS"));
		assertTrue(output.contains("ID"));
		assertTrue(output.contains("NAME"));
	}

	@Test
	public void schemaCommandHandlesUnknownTable() throws Exception {
		BufferedReader reader = readerWith("schema nonexistent\nresume\n");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertTrue(output.contains("Table not found"));
	}

	@Test
	public void eofEndsSession() throws Exception {
		BufferedReader reader = readerWith("");
		InsistenceLayerConsole.run(connection, reader, out);

		String output = outputBuffer.toString();
		assertFalse(output.contains("ERROR"));
	}

	private BufferedReader readerWith(String input) {
		return new BufferedReader(new StringReader(input));
	}

}
