package org.simulatest.example.library.util;

import org.h2.jdbcx.JdbcDataSource;
import org.simulatest.example.library.LibraryDatabase;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

/**
 * Test infrastructure setup: H2 in-memory database + schema.
 *
 * Called from a static block in each test class. Always reconfigures
 * the DataSource and recreates the schema so that library tests use
 * the correct database regardless of prior JVM state.
 */
public final class LibraryTestSetup {

	private LibraryTestSetup() {
		// Static methods only
	}

	public static synchronized void init() {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:library;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerDataSource.configure(h2);

		LibraryDatabase.createSchema();
	}

}
