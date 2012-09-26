package org.simulatest.insistencelayer;

import java.sql.Connection;
import java.util.HashMap;
import java.util.Map;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class InsistenceLayerManagerFactory {
	
	private static Map<Connection, InsistenceLayerManager> cache;
	
	static {
		cache = new HashMap<Connection, InsistenceLayerManager>();
	}
	
	public static InsistenceLayerManager build(ConnectionWrapper connection) {
		if (cache.get(connection) == null) cache.put(connection, new InsistenceLayerManager(connection));
		
		return cache.get(connection);
	}

}