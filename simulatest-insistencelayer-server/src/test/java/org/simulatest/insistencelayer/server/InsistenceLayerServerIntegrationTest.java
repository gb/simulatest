package org.simulatest.insistencelayer.server;

import static org.junit.Assert.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.sql.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.server.client.RemoteDataSource;

public class InsistenceLayerServerIntegrationTest {

	private Connection serverConnection;
	private InsistenceLayerServer server;
	private RemoteDataSource remoteDataSource;
	private HttpClient httpClient;
	private int port;

	@Before
	public void setUp() throws Exception {
		serverConnection = DriverManager.getConnection("jdbc:h2:mem:integrationtest;DB_CLOSE_DELAY=-1");

		// Create table directly on the server connection
		try (Statement stmt = serverConnection.createStatement()) {
			stmt.execute("DROP TABLE IF EXISTS USERS");
			stmt.execute("CREATE TABLE USERS (ID INT PRIMARY KEY, NAME VARCHAR(100))");
		}

		server = new InsistenceLayerServer(0, serverConnection);
		server.start();
		port = server.getPort();

		remoteDataSource = new RemoteDataSource(port);
		httpClient = HttpClient.newHttpClient();
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		serverConnection.close();
	}

	@Test
	public void shouldExecuteInsertAndQuery() throws Exception {
		try (Connection conn = remoteDataSource.getConnection()) {
			// Insert via prepared statement
			try (PreparedStatement ps = conn.prepareStatement("INSERT INTO USERS (ID, NAME) VALUES (?, ?)")) {
				ps.setInt(1, 1);
				ps.setString(2, "Alice");
				assertEquals(1, ps.executeUpdate());
			}

			// Query back
			try (PreparedStatement ps = conn.prepareStatement("SELECT ID, NAME FROM USERS WHERE ID = ?")) {
				ps.setInt(1, 1);
				try (ResultSet rs = ps.executeQuery()) {
					assertTrue(rs.next());
					assertEquals(1, rs.getInt("ID"));
					assertEquals("Alice", rs.getString("NAME"));
					assertFalse(rs.next());
				}
			}
		}
	}

	@Test
	public void shouldExecuteInsertAndQueryViaStatement() throws Exception {
		try (Connection conn = remoteDataSource.getConnection()) {
			try (Statement stmt = conn.createStatement()) {
				assertEquals(1, stmt.executeUpdate("INSERT INTO USERS (ID, NAME) VALUES (10, 'Bob')"));

				try (ResultSet rs = stmt.executeQuery("SELECT NAME FROM USERS WHERE ID = 10")) {
					assertTrue(rs.next());
					assertEquals("Bob", rs.getString("NAME"));
				}
			}
		}
	}

	@Test
	public void shouldGetCurrentLevel() throws Exception {
		String body = get("/level");
		assertTrue(body.contains("\"level\""));
		assertTrue(body.contains("0"));
	}

	@Test
	public void shouldIncreaseLevelAndRollbackOnDecrease() throws Exception {
		// Insert a row at level 0
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO USERS (ID, NAME) VALUES (100, 'BaseData')");
		}

		// Increase level
		String response = post("/level/increase", "");
		assertTrue(response.contains("\"level\""));
		assertTrue(response.contains("1"));

		// Insert at level 1
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO USERS (ID, NAME) VALUES (101, 'Level1Data')");

			// Verify it exists
			try (ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS WHERE ID = 101")) {
				assertTrue(rs.next());
				assertEquals(1, rs.getInt(1));
			}
		}

		// Decrease level → should rollback the insert at level 1
		response = post("/level/decrease", "");
		assertTrue(response.contains("0"));

		// Verify level 1 data is gone
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS WHERE ID = 101")) {
			assertTrue(rs.next());
			assertEquals(0, rs.getInt(1));
		}

		// But base data survives
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS WHERE ID = 100")) {
			assertTrue(rs.next());
			assertEquals(1, rs.getInt(1));
		}
	}

	@Test
	public void shouldResetCurrentLevel() throws Exception {
		post("/level/increase", "");

		// Insert data
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.executeUpdate("INSERT INTO USERS (ID, NAME) VALUES (200, 'ResetMe')");
		}

		// Reset
		post("/level/reset", "");

		// Data should be gone
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement();
			 ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM USERS WHERE ID = 200")) {
			assertTrue(rs.next());
			assertEquals(0, rs.getInt(1));
		}

		// Clean up
		post("/level/decrease", "");
	}

	@Test
	public void shouldReturnSqlErrorOnBadQuery() throws Exception {
		try (Connection conn = remoteDataSource.getConnection();
			 Statement stmt = conn.createStatement()) {
			stmt.executeQuery("SELECT * FROM NONEXISTENT_TABLE");
			fail("Should have thrown SQLException");
		} catch (SQLException e) {
			assertNotNull(e.getMessage());
		}
	}

	@Test
	public void shouldHandleNullValues() throws Exception {
		try (Connection conn = remoteDataSource.getConnection()) {
			try (PreparedStatement ps = conn.prepareStatement("INSERT INTO USERS (ID, NAME) VALUES (?, ?)")) {
				ps.setInt(1, 300);
				ps.setNull(2, Types.VARCHAR);
				assertEquals(1, ps.executeUpdate());
			}

			try (Statement stmt = conn.createStatement();
				 ResultSet rs = stmt.executeQuery("SELECT NAME FROM USERS WHERE ID = 300")) {
				assertTrue(rs.next());
				assertNull(rs.getString("NAME"));
				assertTrue(rs.wasNull());
			}
		}
	}

	private String get(String path) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + path))
			.GET()
			.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
	}

	private String post(String path, String body) throws Exception {
		HttpRequest request = HttpRequest.newBuilder()
			.uri(URI.create("http://localhost:" + port + path))
			.POST(HttpRequest.BodyPublishers.ofString(body))
			.build();
		return httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
	}
}
