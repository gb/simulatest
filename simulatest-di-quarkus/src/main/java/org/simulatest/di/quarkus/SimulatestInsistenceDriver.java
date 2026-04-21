package org.simulatest.di.quarkus;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.DriverPropertyInfo;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Properties;
import java.util.logging.Logger;

import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * JDBC driver that routes every {@link Connection} through the Simulatest
 * Insistence Layer. URLs take the form:
 *
 * <pre>
 *   jdbc:simulatest:&lt;underlying-jdbc-url&gt;
 * </pre>
 *
 * <p>e.g. {@code jdbc:simulatest:jdbc:h2:mem:app;DB_CLOSE_DELAY=-1}. The
 * underlying URL is resolved to a real driver via {@link DriverManager}, a
 * single physical connection is opened against it, and every {@code connect()}
 * call returns the Insistence Layer's {@link Connection} proxy wrapping it.
 *
 * <p>See {@link SimulatestQuarkusPlugin} for Quarkus configuration: required
 * Agroal pool sizing, parent-first classloading, and Hibernate dialect.
 *
 * <h2>Single-configuration contract</h2>
 *
 * <p>The driver configures the Insistence Layer on its first successful
 * connection and reuses that configuration for subsequent {@code connect()}
 * calls. Calls with a different underlying URL fail fast with a
 * {@link SQLException} rather than silently routing to the first-configured
 * database. If the first connection attempt fails (e.g. bad credentials,
 * temporary outage), no configuration is retained; a later call with valid
 * parameters configures cleanly.
 *
 * <h2>Database-specific behavior</h2>
 *
 * <p>Tested against H2 and PostgreSQL. Engines that cascade-release newer
 * savepoints when an older one is released (some older MySQL and Oracle
 * versions) may surface edge cases around the translated-commit savepoint
 * the Insistence Layer bumps on every {@code commit()}. Run the target
 * engine through the library suite before adopting on it.
 */
public final class SimulatestInsistenceDriver implements Driver {

	/** URL scheme prefix recognized by this driver. */
	public static final String URL_PREFIX = "jdbc:simulatest:";

	private static volatile String configuredUnderlyingUrl;

	static {
		try {
			DriverManager.registerDriver(new SimulatestInsistenceDriver());
		} catch (SQLException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	@Override
	public Connection connect(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) return null;
		ensureInsistenceLayerConfigured(url, info);
		return InsistenceLayerFactory.requireDataSource().getConnection();
	}

	@Override
	public boolean acceptsURL(String url) {
		return url != null && url.startsWith(URL_PREFIX);
	}

	@Override
	public DriverPropertyInfo[] getPropertyInfo(String url, Properties info) throws SQLException {
		if (!acceptsURL(url)) return new DriverPropertyInfo[0];

		String underlyingUrl = underlyingUrlOf(url);
		try {
			return DriverManager.getDriver(underlyingUrl).getPropertyInfo(underlyingUrl, info);
		} catch (SQLException unresolved) {
			return new DriverPropertyInfo[0];
		}
	}

	@Override
	public int getMajorVersion() {
		return 0;
	}

	@Override
	public int getMinorVersion() {
		return 1;
	}

	/**
	 * Not JDBC-compliant in the strict sense: commits and rollbacks are
	 * translated into Insistence Layer savepoint operations, which is a
	 * deliberate deviation from pass-through JDBC semantics.
	 */
	@Override
	public boolean jdbcCompliant() {
		return false;
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	// Synchronized so Agroal warm-up threads hitting connect() concurrently
	// can't race into double initialization.
	//
	// When isConfigured() is true, a mismatched URL fails fast rather than
	// silently handing out a connection to the first-configured database.
	//
	// When the underlying DriverBackedDataSource fails to open (bad credentials,
	// outage), InsistenceLayerRegistry.configure throws before mutating its
	// state, so retrying with valid parameters later configures cleanly.
	private static synchronized void ensureInsistenceLayerConfigured(String url, Properties info) throws SQLException {
		String underlyingUrl = underlyingUrlOf(url);
		if (InsistenceLayerFactory.isConfigured()) {
			if (!underlyingUrl.equals(configuredUnderlyingUrl)) {
				throw new SQLException(
					"SimulatestInsistenceDriver was already configured for '" + configuredUnderlyingUrl
					+ "'; cannot switch to '" + underlyingUrl + "' in the same JVM.");
			}
			return;
		}
		InsistenceLayerFactory.configure(new DriverBackedDataSource(underlyingUrl, info));
		configuredUnderlyingUrl = underlyingUrl;
	}

	private static String underlyingUrlOf(String url) {
		return url.substring(URL_PREFIX.length());
	}

}
