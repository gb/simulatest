package org.simulatest.insistencelayer;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;

public class InsistenceLayerFactory {

	public static final String DEFAULT = "default";
	private static final Map<String, InsistenceLayer> registry = new LinkedHashMap<>();
	private static final Map<String, InsistenceLayerDataSource> dataSources = new LinkedHashMap<>();

	public static void configure(DataSource dataSource) {
		var wrapped = new InsistenceLayerDataSource(dataSource);
		dataSources.put(DEFAULT, wrapped);
		registry.put(DEFAULT, build(wrapped.getConnectionWrapper()));
	}

	public static InsistenceLayerDataSource dataSource() {
		return dataSources.isEmpty() ? null : dataSources.values().iterator().next();
	}

	public static InsistenceLayerDataSource requireDataSource() {
		InsistenceLayerDataSource ds = dataSource();
		if (ds == null) {
			throw new IllegalStateException("InsistenceLayer not configured - call InsistenceLayerFactory.configure(dataSource) first");
		}
		return ds;
	}

	public static boolean isConfigured() {
		return !registry.isEmpty();
	}

	public static InsistenceLayer build(ConnectionWrapper connection) {
		return new LocalInsistenceLayer(connection);
	}

	public static void register(String name, InsistenceLayer manager) {
		registry.put(name, manager);
	}

	public static void deregister(String name) {
		registry.remove(name);
	}

	public static InsistenceLayer resolve(String name) {
		return registry.get(name);
	}

	public static InsistenceLayer resolve() {
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
