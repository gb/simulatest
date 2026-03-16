package org.simulatest.insistencelayer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class InsistenceLayerManagerFactory {

	public static final String DEFAULT = "default";
	private static final Map<String, InsistenceLayerManager> registry = new LinkedHashMap<>();
	private static final Map<String, InsistenceLayerDataSource> dataSources = new LinkedHashMap<>();

	public static void configure(DataSource dataSource) {
		var wrapped = new InsistenceLayerDataSource(dataSource);
		dataSources.put(DEFAULT, wrapped);
		registry.put(DEFAULT, build(wrapped.getConnectionWrapper()));
	}

	public static InsistenceLayerDataSource dataSource() {
		return dataSources.isEmpty() ? null : dataSources.values().iterator().next();
	}

	public static boolean isConfigured() {
		return !registry.isEmpty();
	}

	public static InsistenceLayerManager build(ConnectionWrapper connection) {
		return new LocalInsistenceLayerManager(connection);
	}

	public static void register(String name, InsistenceLayerManager manager) {
		registry.put(name, manager);
	}

	public static void deregister(String name) {
		registry.remove(name);
	}

	public static InsistenceLayerManager resolve(String name) {
		return registry.get(name);
	}

	public static InsistenceLayerManager resolve() {
		if (!registry.isEmpty()) {
			return registry.values().iterator().next();
		}

		if (!dataSources.isEmpty()) {
			return build(dataSources.values().iterator().next().getConnectionWrapper());
		}

		return null;
	}

	public static void clear() {
		registry.clear();
		dataSources.clear();
	}

}
