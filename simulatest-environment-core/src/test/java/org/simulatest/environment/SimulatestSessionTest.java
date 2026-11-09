package org.simulatest.environment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.junit.After;
import org.junit.Test;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.testsupport.H2TestDataSources;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

public class SimulatestSessionTest {

	@After
	public void resetInsistenceLayer() {
		InsistenceLayerFactory.clear();
	}

	@Test
	public void closeShouldClearTheInsistenceLayerThePluginsConfigured() {
		SimulatestSession session = SimulatestSession.open(List.of(new DataSourcePlugin()), List.of());

		session.close();

		assertFalse(InsistenceLayerFactory.isConfigured());
	}

	@Test
	public void closeShouldKeepAnInsistenceLayerConfiguredBeforeTheSession() {
		InsistenceLayerFactory.configure(createH2DataSource());
		InsistenceLayerDataSource manualDataSource = InsistenceLayerFactory.dataSource().orElseThrow();
		SimulatestSession session = SimulatestSession.open(List.of(new DataSourcePlugin()), List.of());

		session.close();

		assertSame(manualDataSource, InsistenceLayerFactory.dataSource().orElseThrow());
	}

	@Test
	public void secondSessionShouldConfigureFromItsOwnPlugin() {
		SimulatestSession first = SimulatestSession.open(List.of(new DataSourcePlugin()), List.of());
		InsistenceLayerDataSource firstDataSource = InsistenceLayerFactory.dataSource().orElseThrow();
		first.close();

		SimulatestSession.open(List.of(new DataSourcePlugin()), List.of());

		assertNotSame(firstDataSource, InsistenceLayerFactory.dataSource().orElseThrow());
	}

	@Test
	public void failedOpenShouldClearTheInsistenceLayerThePluginsConfigured() {
		List<SimulatestPlugin> plugins = List.of(new DataSourcePlugin(), new FailingPlugin());

		assertThrows(IllegalStateException.class, () -> SimulatestSession.open(plugins, List.of()));

		assertFalse(InsistenceLayerFactory.isConfigured());
	}

	private static DataSource createH2DataSource() {
		return H2TestDataSources.freshInMemory("session-test");
	}

	// Stands in for a DI plugin: configures the layer from its own DataSource.
	private static final class DataSourcePlugin implements SimulatestPlugin {
		@Override
		public void initialize(Collection<Class<?>> testClasses) {
			if (!InsistenceLayerFactory.isConfigured()) InsistenceLayerFactory.configure(createH2DataSource());
		}
	}

	private static final class FailingPlugin implements SimulatestPlugin {
		@Override
		public void initialize(Collection<Class<?>> testClasses) {
			throw new IllegalStateException("plugin failed to start");
		}
	}

}
