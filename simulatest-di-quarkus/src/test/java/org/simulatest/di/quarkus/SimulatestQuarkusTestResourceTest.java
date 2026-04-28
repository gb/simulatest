package org.simulatest.di.quarkus;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import org.eclipse.microprofile.config.spi.ConfigProviderResolver;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerJdbcDriver;

import io.smallrye.config.PropertiesConfigSource;
import io.smallrye.config.SmallRyeConfigBuilder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Drives {@link SimulatestQuarkusTestResource}'s lifecycle directly to keep
 * this module's tests fast and independent of the Quarkus Maven plugin. A
 * full {@code @QuarkusTest} + Panache integration test lives in
 * {@code simulatest-examples}.
 */
class SimulatestQuarkusTestResourceTest {

	private static final String UNDERLYING_URL = "jdbc:h2:mem:resource-test;DB_CLOSE_DELAY=-1";

	@BeforeEach
	void setUp() throws Exception {
		// Force-register the H2 driver. JUnit Jupiter's surefire fork can run
		// before any class on the classpath has triggered org.h2.Driver's static
		// initializer, so DriverManager.getDrivers() may not include it yet.
		Class.forName("org.h2.Driver");

		InsistenceLayerFactory.clear();
		StubBootstrap.applySchemaCalls = 0;
		StubBootstrap.lastWrappedDataSource = null;

		ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
		ConfigProviderResolver resolver = ConfigProviderResolver.instance();
		try {
			resolver.releaseConfig(resolver.getConfig(classLoader));
		} catch (IllegalStateException none) {
			// no config registered yet for this classloader
		}
		resolver.registerConfig(
			new SmallRyeConfigBuilder()
				.withSources(new PropertiesConfigSource(
					Map.of(
						"quarkus.datasource.jdbc.url", UNDERLYING_URL,
						"quarkus.datasource.username", "sa",
						"quarkus.datasource.password", ""),
					"test",
					100))
				.build(),
			classLoader);
	}

	@AfterEach
	void tearDown() throws SQLException {
		try (Connection c = java.sql.DriverManager.getConnection(UNDERLYING_URL, "sa", "");
			 Statement s = c.createStatement()) {
			s.execute("DROP ALL OBJECTS");
		} catch (SQLException ignore) {
			// teardown best-effort; driver may not be registered if setUp failed
		}
		InsistenceLayerFactory.clear();
	}

	@Test
	void startPublishesInsistenceLayerJdbcDriverOverrides() {
		Map<String, String> overrides = withoutBootstrap().start();

		assertEquals(
			InsistenceLayerJdbcDriver.URL_PREFIX + UNDERLYING_URL,
			overrides.get("quarkus.datasource.jdbc.url"),
			"the URL the user wrote in application.properties must be wrapped with our prefix");
		assertEquals(
			InsistenceLayerJdbcDriver.class.getName(),
			overrides.get("quarkus.datasource.jdbc.driver"),
			"the driver Quarkus loads must be ours, transparently");
	}

	@Test
	void startConfiguresInsistenceLayerSoEnvironmentsCanResolveIt() {
		withoutBootstrap().start();

		assertTrue(InsistenceLayerFactory.isConfigured(),
			"the Insistence Layer must be configured before any environment runs");
	}

	@Test
	void startInvokesUserSuppliedSchemaApplierWhenABootstrapIsRegistered() {
		withBootstrap(new StubBootstrap()).start();

		assertEquals(1, StubBootstrap.applySchemaCalls,
			"applySchema must be called exactly once during start()");
		assertEquals(
			InsistenceLayerFactory.requireDataSource(),
			StubBootstrap.lastWrappedDataSource,
			"applySchema must receive the Insistence-Layer-wrapped DataSource");
	}

	@Test
	void startSkipsSchemaWhenNoBootstrapIsRegistered() {
		withoutBootstrap().start();

		assertEquals(0, StubBootstrap.applySchemaCalls,
			"with no bootstrap registered, schema application is the user's responsibility "
			+ "(Hibernate drop-and-create, Flyway, Liquibase, etc.)");
		assertTrue(InsistenceLayerFactory.isConfigured(),
			"the Insistence Layer must still be configured even without a bootstrap");
	}

	@Test
	void stopClearsTheInsistenceLayerSoARestartStartsClean() {
		SimulatestQuarkusTestResource resource = withoutBootstrap();
		resource.start();
		assertTrue(InsistenceLayerFactory.isConfigured());

		resource.stop();

		assertFalse(InsistenceLayerFactory.isConfigured(),
			"stop() must clear the registry so a second @QuarkusTest session in the same "
			+ "JVM (e.g. @TestProfile switch) reconfigures cleanly");
	}

	private static SimulatestQuarkusTestResource withoutBootstrap() {
		return new SimulatestQuarkusTestResource(Optional.empty());
	}

	private static SimulatestQuarkusTestResource withBootstrap(SimulatestQuarkusBootstrap bootstrap) {
		return new SimulatestQuarkusTestResource(Optional.of(bootstrap));
	}

	public static final class StubBootstrap implements SimulatestQuarkusBootstrap {
		static int applySchemaCalls;
		static DataSource lastWrappedDataSource;

		@Override
		public void applySchema(DataSource wrapped) {
			applySchemaCalls++;
			lastWrappedDataSource = wrapped;
		}
	}

}
