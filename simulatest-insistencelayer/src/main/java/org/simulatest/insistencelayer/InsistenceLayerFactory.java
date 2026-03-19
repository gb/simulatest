package org.simulatest.insistencelayer;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

/**
 * Static facade over a shared {@link InsistenceLayerRegistry}.
 *
 * <p>All static methods delegate to a default registry. For independent
 * registries (parallel execution, multi-datasource), create your own
 * {@link InsistenceLayerRegistry} instance.</p>
 */
public class InsistenceLayerFactory {

	public static final String DEFAULT = InsistenceLayerRegistry.DEFAULT;

	private static final InsistenceLayerRegistry defaultRegistry = new InsistenceLayerRegistry();

	public static InsistenceLayerRegistry defaultRegistry() {
		return defaultRegistry;
	}

	public static void configure(DataSource dataSource) {
		defaultRegistry.configure(dataSource);
	}

	public static InsistenceLayerDataSource dataSource() {
		return defaultRegistry.dataSource();
	}

	public static InsistenceLayerDataSource requireDataSource() {
		return defaultRegistry.requireDataSource();
	}

	public static boolean isConfigured() {
		return defaultRegistry.isConfigured();
	}

	public static InsistenceLayer build(ConnectionWrapper connection) {
		return new LocalInsistenceLayer(connection);
	}

	public static void register(String name, InsistenceLayer manager) {
		defaultRegistry.register(name, manager);
	}

	public static void deregister(String name) {
		defaultRegistry.deregister(name);
	}

	public static InsistenceLayer resolve(String name) {
		return defaultRegistry.resolve(name);
	}

	public static InsistenceLayer resolve() {
		return defaultRegistry.resolve();
	}

	public static void clear() {
		defaultRegistry.clear();
	}

}
