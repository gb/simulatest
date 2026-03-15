package org.simulatest.insistencelayer;

import org.h2.jdbcx.JdbcDataSource;

public final class TestDataSources {

	public static JdbcDataSource createH2(String dbName) {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:" + dbName + ";DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		return h2;
	}

	private TestDataSources() {}

}
