package org.simulatest.example.library;

import java.util.Collection;

import org.h2.jdbcx.JdbcDataSource;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;

/**
 * Configures the H2 in-memory database and creates the schema before the
 * environment tree runs. Discovered via {@link java.util.ServiceLoader}.
 */
public class LibraryPlugin implements SimulatestPlugin {

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:library;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerManagerFactory.configure(h2);

		LibraryDatabase.createSchema();
	}

}
