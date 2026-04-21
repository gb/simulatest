package org.simulatest.di.quarkus;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.Environment;
import org.simulatest.environment.junit5.DeferredEnvironmentCoordinator;
import org.simulatest.environment.junit5.DeferredEnvironmentLifecycle;
import org.simulatest.environment.plugin.EnvironmentLifecycle;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * End-to-end checks for the plugin plus the driver — no Quarkus runtime, but
 * enough coverage that the Agroal-compatible surface actually behaves.
 *
 * <p>A real Quarkus integration test with Panache lives in
 * {@code simulatest-examples} where the Quarkus Maven plugin and runtime
 * dependencies are already justified.
 */
class SimulatestQuarkusPluginTest {

	private static final String UNDERLYING_URL = "jdbc:h2:mem:quarkus-plugin-test;DB_CLOSE_DELAY=-1";
	private static final String SIMULATEST_URL = SimulatestInsistenceDriver.URL_PREFIX + UNDERLYING_URL;

	private JdbcDataSource rawDataSource;

	@BeforeEach
	void setUp() {
		rawDataSource = new JdbcDataSource();
		rawDataSource.setURL(UNDERLYING_URL);
		rawDataSource.setUser("sa");

		TestConfigurer.rawDataSource = rawDataSource;
		TestConfigurer.schemaApplied = false;

		InsistenceLayerFactory.clear();
	}

	@AfterEach
	void tearDown() throws SQLException {
		InsistenceLayerFactory.clear();
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("DROP ALL OBJECTS");
		}
	}

	// =========================================================================
	// Plugin — bootstrapping and schema application
	// =========================================================================

	@Test
	void pluginConfiguresInsistenceLayerFromTheConfigurer() {
		new SimulatestQuarkusPlugin().initialize(List.of());

		assertTrue(InsistenceLayerFactory.isConfigured());
		assertInstanceOf(InsistenceLayerDataSource.class, InsistenceLayerFactory.requireDataSource());
	}

	@Test
	void pluginAppliesUserSuppliedSchemaBeforeAnyEnvironmentRuns() {
		new SimulatestQuarkusPlugin().initialize(List.of());
		assertTrue(TestConfigurer.schemaApplied);

		DataSource wrapped = InsistenceLayerFactory.requireDataSource();
		try (Connection c = wrapped.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("INSERT INTO book (id, title) VALUES (1, 'Setup')");
			assertEquals(1, countRows(wrapped, "SELECT COUNT(*) FROM book"));
		} catch (SQLException e) {
			throw new AssertionError("expected schema to be applied", e);
		}
	}

	@Test
	void pluginIsIdempotentWhenInsistenceLayerAlreadyConfigured() {
		InsistenceLayerFactory.configure(rawDataSource);
		DataSource before = InsistenceLayerFactory.requireDataSource();

		new SimulatestQuarkusPlugin().initialize(List.of());

		assertEquals(before, InsistenceLayerFactory.requireDataSource());
	}

	@Test
	void destroyClearsRegistrySoNextRunStartsFresh() {
		new SimulatestQuarkusPlugin().initialize(List.of());
		assertTrue(InsistenceLayerFactory.isConfigured());

		new SimulatestQuarkusPlugin().destroy();

		assertFalse(InsistenceLayerFactory.isConfigured(),
			"destroy() must clear the registry so a second run can reconfigure cleanly");
	}

	@Test
	void pluginContributesDeferredLifecycle() {
		EnvironmentLifecycle lifecycle = new SimulatestQuarkusPlugin().environmentLifecycle();

		assertInstanceOf(DeferredEnvironmentLifecycle.class, lifecycle,
			"Quarkus plugin must defer env execution so Arc is up when envs run");
	}

	@Test
	void destroyAlsoClearsCoordinatorStateSoARestartStartsFresh() {
		DeferredEnvironmentCoordinator.claimNotYetRun(FakeEnv.class);

		new SimulatestQuarkusPlugin().destroy();

		assertTrue(DeferredEnvironmentCoordinator.claimNotYetRun(FakeEnv.class),
			"destroy() must clear coordinator state, otherwise a second suite in the same "
			+ "JVM would skip environments that look like they already ran");
	}

	static class FakeEnv implements Environment {
		@Override public void run() {}
	}

	@Test
	void validationRejectsZeroConfigurers() {
		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> SimulatestQuarkusPlugin.requireExactlyOneConfigurer(List.of()));

		assertTrue(e.getMessage().contains("META-INF/services"),
			"error message should direct the user at the service registration: " + e.getMessage());
	}

	@Test
	void validationRejectsMultipleConfigurers() {
		QuarkusSimulatestConfigurer a = new TestConfigurer();
		QuarkusSimulatestConfigurer b = new TestConfigurer();

		IllegalStateException e = assertThrows(IllegalStateException.class,
			() -> SimulatestQuarkusPlugin.requireExactlyOneConfigurer(List.of(a, b)));

		assertTrue(e.getMessage().contains("Multiple"),
			"error message should name both implementations: " + e.getMessage());
	}

	@Test
	void validationReturnsTheSoleConfigurerOtherwise() {
		QuarkusSimulatestConfigurer only = new TestConfigurer();

		QuarkusSimulatestConfigurer returned =
			SimulatestQuarkusPlugin.requireExactlyOneConfigurer(List.of(only));

		assertEquals(only, returned);
	}

	// =========================================================================
	// Driver — URL recognition, registration, and routing
	// =========================================================================

	@Test
	void driverAcceptsSimulatestUrls() {
		SimulatestInsistenceDriver driver = new SimulatestInsistenceDriver();
		assertTrue(driver.acceptsURL(SIMULATEST_URL));
		assertTrue(driver.acceptsURL("jdbc:simulatest:jdbc:postgresql://host/db"));
	}

	@Test
	void driverRejectsOtherUrls() {
		SimulatestInsistenceDriver driver = new SimulatestInsistenceDriver();
		assertFalse(driver.acceptsURL("jdbc:h2:mem:test"));
		assertFalse(driver.acceptsURL("jdbc:postgresql://host/db"));
		assertFalse(driver.acceptsURL(null));
	}

	@Test
	void driverConnectReturnsNullForNonSimulatestUrl() throws SQLException {
		assertNull(new SimulatestInsistenceDriver().connect("jdbc:h2:mem:x", null));
	}

	@Test
	void driverManagerResolvesSimulatestUrlToOurDriver() throws SQLException {
		new SimulatestQuarkusPlugin().initialize(List.of());

		try (Connection conn = DriverManager.getConnection(SIMULATEST_URL)) {
			assertNotNull(conn);

			try (Statement stmt = conn.createStatement()) {
				stmt.execute("INSERT INTO book (id, title) VALUES (7, 'Driver-loaded')");
			}

			assertEquals(1, countRows(InsistenceLayerFactory.requireDataSource(),
				"SELECT COUNT(*) FROM book WHERE id = 7"));
		}
	}

	@Test
	void driverLazilyConfiguresInsistenceLayerWhenPluginDidNotRun() throws SQLException {
		// Plugin intentionally NOT invoked. We still need the schema to exist for
		// the following INSERT, so create it directly on the underlying connection.
		try (Connection c = rawDataSource.getConnection();
			 Statement s = c.createStatement()) {
			s.execute("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
		}

		assertFalse(InsistenceLayerFactory.isConfigured());

		// Credentials passed explicitly — DriverManager.getConnection(url) alone
		// relies on the URL carrying them, which not every underlying driver supports.
		try (Connection conn = DriverManager.getConnection(SIMULATEST_URL, "sa", "");
			 Statement s = conn.createStatement()) {
			s.execute("INSERT INTO book (id, title) VALUES (9, 'Lazy')");
		}

		assertTrue(InsistenceLayerFactory.isConfigured(),
			"driver must auto-configure the Insistence Layer when invoked cold");
	}

	@Test
	void savepointRollbackWorksThroughTheDriver() throws SQLException {
		new SimulatestQuarkusPlugin().initialize(List.of());

		InsistenceLayer layer = InsistenceLayerFactory.resolve().orElseThrow();

		layer.increaseLevel();
		try (Connection c = DriverManager.getConnection(SIMULATEST_URL);
			 Statement s = c.createStatement()) {
			s.execute("INSERT INTO book (id, title) VALUES (42, 'Temporary')");
		}
		assertEquals(1, countRows(InsistenceLayerFactory.requireDataSource(),
			"SELECT COUNT(*) FROM book WHERE id = 42"));

		layer.decreaseLevel();

		assertEquals(0, countRows(InsistenceLayerFactory.requireDataSource(),
			"SELECT COUNT(*) FROM book WHERE id = 42"),
			"savepoint rollback must undo work done through the driver too");
	}

	@Test
	void pluginAndDriverShareSameConnection() throws SQLException {
		new SimulatestQuarkusPlugin().initialize(List.of());

		try (Connection viaPlugin = InsistenceLayerFactory.requireDataSource().getConnection();
			 Connection viaDriver = DriverManager.getConnection(SIMULATEST_URL)) {

			try (Statement s = viaPlugin.createStatement()) {
				s.execute("INSERT INTO book (id, title) VALUES (1, 'Plugin')");
			}

			try (Statement s = viaDriver.createStatement();
				 var rs = s.executeQuery("SELECT title FROM book WHERE id = 1")) {
				assertTrue(rs.next());
				assertEquals("Plugin", rs.getString(1),
					"driver-side Connection must see data written through plugin-side Connection");
			}
		}
	}

	// =========================================================================
	// Helpers and test fixtures
	// =========================================================================

	private static int countRows(DataSource ds, String sql) throws SQLException {
		try (Connection c = ds.getConnection();
			 Statement s = c.createStatement();
			 var rs = s.executeQuery(sql)) {
			rs.next();
			return rs.getInt(1);
		}
	}

	public static final class TestConfigurer implements QuarkusSimulatestConfigurer {

		static DataSource rawDataSource;
		static boolean schemaApplied;

		@Override
		public DataSource dataSource() {
			return rawDataSource;
		}

		@Override
		public void applySchema(DataSource wrapped) {
			try (Connection c = wrapped.getConnection();
				 Statement s = c.createStatement()) {
				s.execute("CREATE TABLE IF NOT EXISTS book (id BIGINT PRIMARY KEY, title VARCHAR(200))");
				schemaApplied = true;
			} catch (SQLException e) {
				throw new IllegalStateException("failed to apply schema", e);
			}
		}
	}

}
