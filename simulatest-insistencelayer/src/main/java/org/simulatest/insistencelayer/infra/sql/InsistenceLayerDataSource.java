package org.simulatest.insistencelayer.infra.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

import javax.sql.DataSource;

/**
 * {@link DataSource} adapter that routes every {@link Connection} through a
 * single shared {@link ConnectionWrapper}, so callers participate in the
 * Insistence Layer's savepoint stack.
 *
 * <p>Per-call credentials are not supported (configure them on the wrapped
 * DataSource); calling {@link #getConnection(String, String)} throws
 * {@link SQLFeatureNotSupportedException}. Like {@link ConnectionWrapper}, the
 * underlying single connection is not thread-safe.</p>
 */
public final class InsistenceLayerDataSource implements DataSource {

	private final DataSource delegate;
	private final ConnectionWrapper connectionWrapper;

	public InsistenceLayerDataSource(DataSource delegate) {
		Objects.requireNonNull(delegate, "DataSource is null");
		this.delegate = delegate;
		this.connectionWrapper = new ConnectionWrapper(delegate);
	}

	/**
	 * Exposes the {@link ConnectionWrapper} backing this data source, so callers
	 * (typically an {@link org.simulatest.insistencelayer.InsistenceLayer}) can
	 * drive savepoints on the same connection that test code uses.
	 *
	 * @return the shared connection wrapper
	 */
	public ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connectionWrapper.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		throw new SQLFeatureNotSupportedException(
				"Per-call credentials are not supported: the Insistence Layer reuses a single connection "
				+ "configured on the wrapped DataSource. Configure credentials on the underlying DataSource.");
	}

	@Override
	public PrintWriter getLogWriter() throws SQLException {
		return delegate.getLogWriter();
	}

	@Override
	public void setLogWriter(PrintWriter out) throws SQLException {
		delegate.setLogWriter(out);
	}

	@Override
	public void setLoginTimeout(int seconds) throws SQLException {
		delegate.setLoginTimeout(seconds);
	}

	@Override
	public int getLoginTimeout() throws SQLException {
		return delegate.getLoginTimeout();
	}

	@Override
	public <T> T unwrap(Class<T> iface) throws SQLException {
		return delegate.unwrap(iface);
	}

	@Override
	public boolean isWrapperFor(Class<?> iface) throws SQLException {
		return delegate.isWrapperFor(iface);
	}

	@Override
	public Logger getParentLogger() throws SQLFeatureNotSupportedException {
		throw new SQLFeatureNotSupportedException();
	}

}
