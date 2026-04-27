package org.simulatest.environment.testsupport;

import javax.sql.DataSource;

import org.h2.jdbcx.JdbcDataSource;

/**
 * Unique in-memory H2 {@link DataSource} per call, so plugin tests don't
 * share state through the JDBC URL.
 */
public final class H2TestDataSources {

	private H2TestDataSources() {}

	public static DataSource freshInMemory(String label) {
		JdbcDataSource ds = new JdbcDataSource();
		ds.setURL("jdbc:h2:mem:" + label + "-" + System.nanoTime() + ";DB_CLOSE_DELAY=-1");
		ds.setUser("sa");
		return ds;
	}

}
