package org.simulatest.insistencelayer;

import java.util.HashMap;
import java.util.Map;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class InsistenceLayerManagerFactory {

	private static Map<ConnectionWrapper, InsistenceLayerManager> cache;

	static {
		cache = new HashMap<ConnectionWrapper, InsistenceLayerManager>();
	}
	
	public static InsistenceLayerManager build(ConnectionWrapper connection) {
		if (cache.get(connection) == null) cache.put(connection, new InsistenceLayerManager(connection));
		
		return cache.get(connection);
	}

}