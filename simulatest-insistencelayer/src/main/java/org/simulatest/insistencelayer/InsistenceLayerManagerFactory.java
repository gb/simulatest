package org.simulatest.insistencelayer;

import java.util.HashMap;
import java.util.Map;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class InsistenceLayerManagerFactory {

	private static final Map<ConnectionWrapper, InsistenceLayerManager> cache = new HashMap<>();

	public static InsistenceLayerManager build(ConnectionWrapper connection) {
		return cache.computeIfAbsent(connection, InsistenceLayerManager::new);
	}

}
