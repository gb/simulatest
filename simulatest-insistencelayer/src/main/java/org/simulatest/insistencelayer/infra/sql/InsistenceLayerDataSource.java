package org.simulatest.insistencelayer.infra.sql;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class InsistenceLayerDataSource implements DataSource {

	private final DataSource delegate;
	private final ConnectionWrapper connectionWrapper;

	public InsistenceLayerDataSource(DataSource delegate) {
		Objects.requireNonNull(delegate, "DataSource is null");
		this.delegate = delegate;
		this.connectionWrapper = new ConnectionWrapper(delegate);
	}

	public ConnectionWrapper getConnectionWrapper() {
		return connectionWrapper;
	}

	@Override
	public Connection getConnection() throws SQLException {
		return connectionWrapper.getConnection();
	}

	@Override
	public Connection getConnection(String username, String password) throws SQLException {
		return getConnection();
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
