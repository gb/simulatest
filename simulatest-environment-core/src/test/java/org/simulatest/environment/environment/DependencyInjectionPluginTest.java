package org.simulatest.environment.environment;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Collection;
import java.util.List;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.After;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;

public class DependencyInjectionPluginTest {

	@After
	public void resetInsistenceLayer() {
		InsistenceLayerManagerFactory.clear();
	}

	@Test
	public void shouldAutoConfigureInsistenceLayerWhenContextProvidesDataSource() {
		var plugin = new DependencyInjectionPlugin(new StubContext(createH2DataSource()));
		plugin.initialize(List.of());

		assertTrue(InsistenceLayerManagerFactory.isConfigured());
	}

	@Test
	public void shouldNotConfigureWhenContextProvidesNoDataSource() {
		var plugin = new DependencyInjectionPlugin(new StubContext(null));
		plugin.initialize(List.of());

		assertFalse(InsistenceLayerManagerFactory.isConfigured());
	}

	@Test
	public void shouldNotOverrideManualConfiguration() {
		InsistenceLayerManagerFactory.configure(createH2DataSource());
		var manualDataSource = InsistenceLayerManagerFactory.dataSource();

		var plugin = new DependencyInjectionPlugin(new StubContext(createH2DataSource()));
		plugin.initialize(List.of());

		assertSame(manualDataSource, InsistenceLayerManagerFactory.dataSource());
	}

	private static JdbcDataSource createH2DataSource() {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:plugin-test-" + System.nanoTime());
		ds.setUser("sa");
		return ds;
	}

	private static class StubContext implements DependencyInjectionContext {

		private final DataSource dataSource;

		StubContext(DataSource dataSource) {
			this.dataSource = dataSource;
		}

		@Override public <T> T getInstance(Class<T> clazz) { throw new UnsupportedOperationException(); }
		@Override public void injectMembers(Object instance) { throw new UnsupportedOperationException(); }
		@Override public void initialize(Collection<Class<?>> testClasses) { /* no-op: stub has no container to bootstrap */ }
		@Override public void destroy() { /* no-op: stub has no container to tear down */ }
		@Override public DataSource dataSource() { return dataSource; }
	}

}
