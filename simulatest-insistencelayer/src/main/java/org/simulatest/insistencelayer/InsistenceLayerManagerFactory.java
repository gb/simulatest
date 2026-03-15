package org.simulatest.insistencelayer;

import java.util.HashMap;
import java.util.Map;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class InsistenceLayerManagerFactory {

	private static final Map<ConnectionWrapper, InsistenceLayerManager> cache = new HashMap<>();

	public static InsistenceLayerManager build(ConnectionWrapper connection) {
		return cache.computeIfAbsent(connection, LocalInsistenceLayerManager::new);
	}

	public static InsistenceLayerManager resolve() {
		InsistenceLayerManager configured = InsistenceLayerManagerHolder.get();
		if (configured != null) return configured;

		if (InsistenceLayerDataSource.isConfigured()) {
			return build(InsistenceLayerDataSource.getDefault().getConnectionWrapper());
		}

		return null;
	}

	static void clearCache() {
		cache.clear();
	}

}
