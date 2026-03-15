package org.simulatest.insistencelayer;

import java.util.LinkedHashMap;
import java.util.Map;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class InsistenceLayerManagerFactory {

	public static final String DEFAULT = "default";
	private static final Map<String, InsistenceLayerManager> registry = new LinkedHashMap<>();

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

		if (InsistenceLayerDataSource.isConfigured()) {
			return build(InsistenceLayerDataSource.getDefault().getConnectionWrapper());
		}

		return null;
	}

	static void clear() {
		registry.clear();
	}

}
