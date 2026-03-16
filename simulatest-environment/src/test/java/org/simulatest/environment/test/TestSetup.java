package org.simulatest.environment.test;

import org.h2.jdbcx.JdbcDataSource;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;

public class TestSetup {

	private static boolean initialized = false;

	public static synchronized void configure() {
		if (!initialized) {
			JdbcDataSource h2 = new JdbcDataSource();
			h2.setURL("jdbc:h2:mem:envtest;DB_CLOSE_DELAY=-1");
			h2.setUser("sa");
			InsistenceLayerManagerFactory.configure(h2);
			initialized = true;
		}
	}

}
