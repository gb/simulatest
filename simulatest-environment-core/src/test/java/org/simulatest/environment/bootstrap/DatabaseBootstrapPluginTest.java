package org.simulatest.environment.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;
import org.simulatest.environment.testsupport.H2TestDataSources;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

public class DatabaseBootstrapPluginTest {

	@After
	public void resetInsistenceLayer() {
		InsistenceLayerFactory.clear();
	}

	@Test
	public void shouldNoOpWhenNoSetupIsRegisteredAndDataSourceAlreadyConfigured() {
		InsistenceLayerFactory.configure(createH2DataSource());
		var preconfigured = InsistenceLayerFactory.dataSource().orElseThrow();

		var plugin = new DatabaseBootstrapPlugin(Optional::empty);
		plugin.initialize(List.of());

		assertSame(preconfigured, InsistenceLayerFactory.dataSource().orElseThrow());
	}

	@Test
	public void shouldNoOpQuietlyWhenNothingIsRegisteredAndNoDataSourceConfigured() {
		// Engine self-tests and tests that don't touch a database run with no
		// DataSource at all. The bootstrap plugin must not impose one. If the
		// user actually tries to use the layer at test time,
		// InsistenceLayerFactory.requireDataSource() fails on its own.
		var plugin = new DatabaseBootstrapPlugin(Optional::empty);

		plugin.initialize(List.of());

		assertTrue(InsistenceLayerFactory.dataSource().isEmpty());
	}

	@Test
	public void shouldConfigureDataSourceAndRunSchemaWhenOnlySetupIsRegistered() {
		var setup = new RecordingSetup(createH2DataSource());

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		plugin.initialize(List.of());

		assertTrue(InsistenceLayerFactory.isConfigured());
		assertNotNull(setup.schemaRanAgainst);
		assertSame(InsistenceLayerFactory.requireDataSource(), setup.schemaRanAgainst);
	}

	@Test
	public void shouldSkipDataSourceButStillRunSchemaWhenLayerAlreadyConfiguredByDi() {
		InsistenceLayerFactory.configure(createH2DataSource());
		var diConfigured = InsistenceLayerFactory.requireDataSource();
		var setup = new RecordingSetup(createH2DataSource());

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		plugin.initialize(List.of());

		assertSame("DI-configured DataSource must win",
				diConfigured, InsistenceLayerFactory.requireDataSource());
		assertSame("schema runs against the DI-configured DataSource",
				diConfigured, setup.schemaRanAgainst);
	}

	@Test
	public void shouldFailWhenSetupReturnsNullDataSourceAndNothingElseConfigured() {
		var setup = new RecordingSetup(null);

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		IllegalStateException error = assertThrows(IllegalStateException.class,
				() -> plugin.initialize(List.of()));

		assertTrue(error.getMessage().contains(setup.getClass().getName()));
		assertTrue(error.getMessage().contains("returned null from dataSource()"));
	}

	@Test
	public void shouldWrapExceptionThrownBySetupSchemaWithSetupClassName() {
		var setup = new ThrowingSetup(createH2DataSource(), new RuntimeException("boom"));

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		IllegalStateException error = assertThrows(IllegalStateException.class,
				() -> plugin.initialize(List.of()));

		assertTrue(error.getMessage().contains(setup.getClass().getName()));
		assertTrue(error.getMessage().contains("setupSchema"));
		assertNotNull(error.getCause());
		assertEquals("boom", error.getCause().getMessage());
	}

	@Test
	public void shouldClearLayerOnSchemaFailureWhenPluginConfiguredIt() {
		var setup = new ThrowingSetup(createH2DataSource(), new RuntimeException("boom"));

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		assertThrows(IllegalStateException.class, () -> plugin.initialize(List.of()));

		assertTrue("layer must be cleared so a retry can bootstrap cleanly",
				InsistenceLayerFactory.dataSource().isEmpty());
	}

	@Test
	public void shouldNotClearLayerOnSchemaFailureWhenDiPluginConfiguredIt() {
		InsistenceLayerFactory.configure(createH2DataSource());
		var diConfigured = InsistenceLayerFactory.requireDataSource();
		var setup = new ThrowingSetup(createH2DataSource(), new RuntimeException("boom"));

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		assertThrows(IllegalStateException.class, () -> plugin.initialize(List.of()));

		assertSame("DI-configured layer must not be cleared by a setup-schema failure",
				diConfigured, InsistenceLayerFactory.requireDataSource());
	}

	@Test
	public void shouldRunSchemaAgainstDiDataSourceEvenWhenSetupReturnsNull() {
		InsistenceLayerFactory.configure(createH2DataSource());
		var diConfigured = InsistenceLayerFactory.requireDataSource();
		var setup = new RecordingSetup(null);

		var plugin = new DatabaseBootstrapPlugin(() -> Optional.of(setup));
		plugin.initialize(List.of());

		assertSame(diConfigured, setup.schemaRanAgainst);
	}

	private static DataSource createH2DataSource() {
		return H2TestDataSources.freshInMemory("bootstrap-test");
	}

	private static final class RecordingSetup implements SimulatestDatabaseSetup {
		private final DataSource dataSource;
		DataSource schemaRanAgainst;

		RecordingSetup(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Override
		public DataSource dataSource() {
			return dataSource;
		}

		@Override
		public void setupSchema(DataSource ds) {
			this.schemaRanAgainst = ds;
		}
	}

	private static final class ThrowingSetup implements SimulatestDatabaseSetup {
		private final DataSource dataSource;
		private final RuntimeException toThrow;

		ThrowingSetup(DataSource dataSource, RuntimeException toThrow) {
			this.dataSource = dataSource;
			this.toThrow = toThrow;
		}

		@Override
		public DataSource dataSource() {
			return dataSource;
		}

		@Override
		public void setupSchema(DataSource ds) {
			throw toThrow;
		}
	}

}
