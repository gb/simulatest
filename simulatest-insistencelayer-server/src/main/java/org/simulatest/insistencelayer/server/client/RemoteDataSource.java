package org.simulatest.insistencelayer.server.client;

import java.io.PrintWriter;
import java.net.http.HttpClient;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.logging.Logger;

import javax.sql.DataSource;

public class RemoteDataSource implements DataSource {

	private final HttpClient httpClient;
	private final String baseUrl;

	public RemoteDataSource(String host, int port) {
		this.baseUrl = "http://" + host + ":" + port;
		this.httpClient = HttpClient.newHttpClient();
	}

	public RemoteDataSource(int port) {
		this("localhost", port);
	}

	@Override
	public Connection getConnection() {
		return new RemoteConnection(httpClient, baseUrl);
	}

	@Override
	public Connection getConnection(String username, String password) {
		return getConnection();
	}

	// ── DataSource boilerplate ──

	@Override public PrintWriter getLogWriter() { return null; }
	@Override public void setLogWriter(PrintWriter out) {}
	@Override public void setLoginTimeout(int seconds) {}
	@Override public int getLoginTimeout() { return 0; }
	@Override public Logger getParentLogger() throws SQLFeatureNotSupportedException { throw new SQLFeatureNotSupportedException(); }
	@Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLFeatureNotSupportedException(); }
	@Override public boolean isWrapperFor(Class<?> iface) { return false; }
}
