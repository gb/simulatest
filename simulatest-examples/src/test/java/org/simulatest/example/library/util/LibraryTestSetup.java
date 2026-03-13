package org.simulatest.example.library.util;

import org.h2.jdbcx.JdbcDataSource;
import org.simulatest.example.library.LibraryDatabase;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

/**
 * One-time test infrastructure setup: H2 in-memory database + schema.
 *
 * Called from a static block in each test class. Safe to call multiple
 * times — both the DataSource configuration and schema creation are
 * guarded against re-execution.
 */
public final class LibraryTestSetup {

	private LibraryTestSetup() {
		// Static methods only
	}

	public static synchronized void init() {
		if (!InsistenceLayerDataSource.isConfigured()) {
			JdbcDataSource h2 = new JdbcDataSource();
			h2.setURL("jdbc:h2:mem:library;DB_CLOSE_DELAY=-1");
			h2.setUser("sa");
			InsistenceLayerDataSource.configure(h2);

			LibraryDatabase.createSchema();
		}
	}

}
