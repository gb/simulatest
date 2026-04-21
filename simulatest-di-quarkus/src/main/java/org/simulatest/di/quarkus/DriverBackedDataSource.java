package org.simulatest.di.quarkus;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.Properties;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * Minimal {@link DataSource} that opens {@link Connection}s via
 * {@link DriverManager} using a fixed JDBC URL. Used only by the driver-only
 * bootstrap path; not intended as a general-purpose {@code DataSource}.
 */
final class DriverBackedDataSource implements DataSource {

	private final String url;
	private final Properties properties;

	DriverBackedDataSource(String url, Properties properties) {
		this.url = Objects.requireNonNull(url, "url must not be null");
		// Defensive copy: the caller may keep a reference to the Properties
		// object and mutate credentials afterwards; we want a stable snapshot.
		Properties snapshot = new Properties();
		if (properties != null) snapshot.putAll(properties);
		this.properties = snapshot;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return DriverManager.getConnection(url, properties);
	}

	@Override
	public Connection getConnection(String user, String password) throws SQLException {
		Properties merged = new Properties();
		merged.putAll(properties);
		if (user != null)     merged.setProperty("user", user);
		if (password != null) merged.setProperty("password", password);
		return DriverManager.getConnection(url, merged);
	}

	@Override public PrintWriter getLogWriter()          { return null; }
	@Override public void setLogWriter(PrintWriter out)   { /* no-op */ }
	@Override public int getLoginTimeout()                { return 0; }
	@Override public void setLoginTimeout(int seconds)    { /* no-op */ }

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		throw new SQLException("DriverBackedDataSource does not wrap anything");
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) {
		return false;
	}

}
