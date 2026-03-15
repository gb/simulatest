package org.simulatest.insistencelayer;

import java.util.HashMap;
import java.util.Map;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class InsistenceLayerManagerFactory {

	private static final Map<ConnectionWrapper, InsistenceLayerManager> cache = new HashMap<>();
	private static InsistenceLayerManager configuredInstance;

	public static void configure(InsistenceLayerManager manager) {
		configuredInstance = manager;
	}

	public static InsistenceLayerManager getConfigured() {
		return configuredInstance;
	}

	public static void reset() {
		configuredInstance = null;
		cache.clear();
	}

	public static InsistenceLayerManager build(ConnectionWrapper connection) {
		return cache.computeIfAbsent(connection, InsistenceLayerManager::new);
	}

}
