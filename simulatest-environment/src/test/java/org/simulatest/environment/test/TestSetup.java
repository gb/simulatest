package org.simulatest.environment.test;

import org.h2.jdbcx.JdbcDataSource;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

public class TestSetup {

	private static boolean initialized = false;

	public static synchronized void configure() {
		if (!initialized) {
			JdbcDataSource h2 = new JdbcDataSource();
			h2.setURL("jdbc:h2:~/.h2/test");
			h2.setUser("sa");
			InsistenceLayerDataSource.configure(h2);
			initialized = true;
		}
	}

}
