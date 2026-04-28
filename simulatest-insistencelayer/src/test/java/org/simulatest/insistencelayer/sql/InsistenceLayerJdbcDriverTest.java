package org.simulatest.insistencelayer.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerJdbcDriver;

public class InsistenceLayerJdbcDriverTest {

	private static final String DB_NAME = "insistencelayer-jdbc-driver-test";
	private static final String UNDERLYING_URL = "jdbc:h2:mem:" + DB_NAME + ";DB_CLOSE_DELAY=-1";
	private static final String INSISTENCE_URL = InsistenceLayerJdbcDriver.URL_PREFIX + UNDERLYING_URL;

	private JdbcDataSource rawDataSource;

	@Before
	public void setUp() {
		rawDataSource = new JdbcDataSource();
		rawDataSource.setURL(UNDERLYING_URL);
		rawDataSource.setUser("sa");
		InsistenceLayerFactory.clear();
	}

	@After
	public void tearDown() throws SQLException {
		InsistenceLayerFactory.clear();
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("DROP ALL OBJECTS");
		}
	}

	@Test
	public void acceptsInsistenceLayerUrls() {
		InsistenceLayerJdbcDriver driver = new InsistenceLayerJdbcDriver();
		assertTrue(driver.acceptsURL(INSISTENCE_URL));
		assertTrue(driver.acceptsURL("jdbc:insistencelayer:jdbc:postgresql://host/db"));
	}

	@Test
	public void rejectsOtherUrls() {
		InsistenceLayerJdbcDriver driver = new InsistenceLayerJdbcDriver();
		assertFalse(driver.acceptsURL("jdbc:h2:mem:test"));
		assertFalse(driver.acceptsURL("jdbc:postgresql://host/db"));
		assertFalse(driver.acceptsURL(null));
	}

	@Test
	public void connectReturnsNullForNonInsistenceLayerUrl() throws SQLException {
		assertNull(new InsistenceLayerJdbcDriver().connect("jdbc:h2:mem:x", null));
	}

	@Test
	public void driverManagerResolvesInsistenceLayerUrlToOurDriver() throws SQLException {
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
		}

		try (Connection conn = DriverManager.getConnection(INSISTENCE_URL, "sa", "")) {
			assertNotNull(conn);
			try (Statement s = conn.createStatement()) {
				s.execute("INSERT INTO book (id, title) VALUES (7, 'Driver-loaded')");
			}
		}

		assertEquals(1, countRows(InsistenceLayerFactory.requireDataSource(),
			"SELECT COUNT(*) FROM book WHERE id = 7"));
	}

	@Test
	public void lazilyConfiguresInsistenceLayerOnFirstConnect() throws SQLException {
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
		}

		assertFalse(InsistenceLayerFactory.isConfigured());

		try (Connection conn = DriverManager.getConnection(INSISTENCE_URL, "sa", "");
			 Statement s = conn.createStatement()) {
			s.execute("INSERT INTO book (id, title) VALUES (9, 'Lazy')");
		}

		assertTrue("driver must auto-configure the Insistence Layer when invoked cold",
			InsistenceLayerFactory.isConfigured());
	}

	@Test
	public void mismatchedUrlAfterFirstConfigFailsFast() throws SQLException {
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
		}

		try (Connection ignored = DriverManager.getConnection(INSISTENCE_URL, "sa", "")) {
			// first connect configures
		}

		String otherUrl = "jdbc:insistencelayer:jdbc:h2:mem:other-db;DB_CLOSE_DELAY=-1";
		try (Connection ignored = DriverManager.getConnection(otherUrl, "sa", "")) {
			fail("driver must reject a second underlying URL once configured");
		} catch (SQLException expected) {
			assertTrue("error must mention both URLs", expected.getMessage().contains(UNDERLYING_URL));
		}
	}

	@Test
	public void savepointRollbackWorksThroughTheDriver() throws SQLException {
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
		}

		// Bootstrap through the driver itself so the factory and the driver's
		// internal static for "configured underlying URL" are aligned. Calling
		// InsistenceLayerFactory.configure(rawDataSource) directly here would
		// configure the factory but leave the driver's static stale, which
		// makes the next driver-issued connect appear as a URL mismatch.
		try (Connection ignored = DriverManager.getConnection(INSISTENCE_URL, "sa", "")) {
			// first connect performs the bootstrap
		}

		InsistenceLayer layer = InsistenceLayerFactory.resolve().orElseThrow();
		DataSource wrapped = InsistenceLayerFactory.requireDataSource();

		layer.increaseLevel();
		try (Connection c = DriverManager.getConnection(INSISTENCE_URL, "sa", "");
			 Statement s = c.createStatement()) {
			s.execute("INSERT INTO book (id, title) VALUES (42, 'Temporary')");
		}
		assertEquals(1, countRows(wrapped, "SELECT COUNT(*) FROM book WHERE id = 42"));

		layer.decreaseLevel();

		assertEquals("savepoint rollback must undo work done through the driver too",
			0, countRows(wrapped, "SELECT COUNT(*) FROM book WHERE id = 42"));
	}

	private static int countRows(DataSource ds, String sql) throws SQLException {
		try (Connection c = ds.getConnection();
			 Statement s = c.createStatement();
			 var rs = s.executeQuery(sql)) {
			rs.next();
			return rs.getInt(1);
		}
	}

}
