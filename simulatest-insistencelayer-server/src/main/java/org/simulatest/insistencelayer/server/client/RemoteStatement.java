package org.simulatest.insistencelayer.server.client;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.simulatest.insistencelayer.server.protocol.ErrorResponse;
import org.simulatest.insistencelayer.server.protocol.JsonUtil;
import org.simulatest.insistencelayer.server.protocol.QueryResponse;
import org.simulatest.insistencelayer.server.protocol.SqlRequest;

public class RemoteStatement implements Statement {

	protected final HttpClient httpClient;
	protected final String baseUrl;
	private boolean closed;
	private ResultSet lastResultSet;
	private int lastUpdateCount = -1;

	public RemoteStatement(HttpClient httpClient, String baseUrl) {
		this.httpClient = httpClient;
		this.baseUrl = baseUrl;
	}

	@Override
	public ResultSet executeQuery(String sql) throws SQLException {
		SqlRequest request = new SqlRequest(sql, List.of());
		QueryResponse response = sendQuery(request);
		lastResultSet = new RemoteResultSet(response);
		return lastResultSet;
	}

	@Override
	public int executeUpdate(String sql) throws SQLException {
		SqlRequest request = new SqlRequest(sql, List.of());
		lastUpdateCount = sendExecute(request);
		return lastUpdateCount;
	}

	@Override
	public boolean execute(String sql) throws SQLException {
		if (isQuery(sql)) {
			executeQuery(sql);
			return true;
		}
		executeUpdate(sql);
		return false;
	}

	protected static boolean isQuery(String sql) {
		String trimmed = sql.trim().toUpperCase();
		return trimmed.startsWith("SELECT") || trimmed.startsWith("WITH");
	}

	protected int sendExecute(SqlRequest request) throws SQLException {
		String json = JsonUtil.sqlRequestToJson(request);
		String responseBody = post("/sql/execute", json);
		Map<String, Object> map = JsonUtil.parseObject(responseBody);
		return ((Number) map.get("updateCount")).intValue();
	}

	protected QueryResponse sendQuery(SqlRequest request) throws SQLException {
		String json = JsonUtil.sqlRequestToJson(request);
		String responseBody = post("/sql/query", json);
		return JsonUtil.parseQueryResponse(responseBody);
	}

	protected String post(String path, String body) throws SQLException {
		try {
			HttpRequest httpRequest = HttpRequest.newBuilder()
				.uri(URI.create(baseUrl + path))
				.header("Content-Type", "application/json")
				.POST(HttpRequest.BodyPublishers.ofString(body, StandardCharsets.UTF_8))
				.build();

			HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

			if (response.statusCode() >= 400) {
				try {
					ErrorResponse err = JsonUtil.parseErrorResponse(response.body());
					throw new SQLException(err.message(), err.sqlState(), err.errorCode());
				} catch (IllegalArgumentException e) {
					throw new SQLException("Server error: " + response.body());
				}
			}

			return response.body();
		} catch (SQLException e) {
			throw e;
		} catch (Exception e) {
			throw new SQLException("HTTP request failed: " + e.getMessage(), e);
		}
	}

	// ── Basic getters ──

	@Override public ResultSet getResultSet() { return lastResultSet; }
	@Override public int getUpdateCount() { return lastUpdateCount; }
	@Override public void close() { closed = true; }
	@Override public boolean isClosed() { return closed; }
	@Override public SQLWarning getWarnings() { return null; }
	@Override public void clearWarnings() {}
	@Override public int getMaxRows() { return 0; }
	@Override public void setMaxRows(int max) {}
	@Override public int getQueryTimeout() { return 0; }
	@Override public void setQueryTimeout(int seconds) {}
	@Override public void setFetchSize(int rows) {}
	@Override public int getFetchSize() { return 0; }
	@Override public void setFetchDirection(int direction) {}
	@Override public int getFetchDirection() { return ResultSet.FETCH_FORWARD; }
	@Override public int getResultSetConcurrency() { return ResultSet.CONCUR_READ_ONLY; }
	@Override public int getResultSetType() { return ResultSet.TYPE_FORWARD_ONLY; }
	@Override public int getResultSetHoldability() { return ResultSet.CLOSE_CURSORS_AT_COMMIT; }
	@Override public Connection getConnection() { return null; }
	@Override public boolean getMoreResults() { return false; }
	@Override public boolean getMoreResults(int current) { return false; }
	@Override public int getMaxFieldSize() { return 0; }
	@Override public void setMaxFieldSize(int max) {}
	@Override public void setEscapeProcessing(boolean enable) {}
	@Override public void setCursorName(String name) throws SQLException { throw unsupported(); }
	@Override public void cancel() throws SQLException { throw unsupported(); }
	@Override public void setPoolable(boolean poolable) {}
	@Override public boolean isPoolable() { return false; }
	@Override public void closeOnCompletion() {}
	@Override public boolean isCloseOnCompletion() { return false; }
	@Override public ResultSet getGeneratedKeys() throws SQLException { throw unsupported(); }
	@Override public void addBatch(String sql) throws SQLException { throw unsupported(); }
	@Override public void clearBatch() throws SQLException { throw unsupported(); }
	@Override public int[] executeBatch() throws SQLException { throw unsupported(); }
	@Override public int executeUpdate(String sql, int autoGeneratedKeys) throws SQLException { return executeUpdate(sql); }
	@Override public int executeUpdate(String sql, int[] columnIndexes) throws SQLException { return executeUpdate(sql); }
	@Override public int executeUpdate(String sql, String[] columnNames) throws SQLException { return executeUpdate(sql); }
	@Override public boolean execute(String sql, int autoGeneratedKeys) throws SQLException { return execute(sql); }
	@Override public boolean execute(String sql, int[] columnIndexes) throws SQLException { return execute(sql); }
	@Override public boolean execute(String sql, String[] columnNames) throws SQLException { return execute(sql); }
	@Override public long getLargeUpdateCount() { return lastUpdateCount; }
	@Override public long getLargeMaxRows() { return 0; }
	@Override public void setLargeMaxRows(long max) {}
	@Override public long[] executeLargeBatch() throws SQLException { throw unsupported(); }
	@Override public long executeLargeUpdate(String sql) throws SQLException { return executeUpdate(sql); }
	@Override public long executeLargeUpdate(String sql, int autoGeneratedKeys) throws SQLException { return executeUpdate(sql); }
	@Override public long executeLargeUpdate(String sql, int[] columnIndexes) throws SQLException { return executeUpdate(sql); }
	@Override public long executeLargeUpdate(String sql, String[] columnNames) throws SQLException { return executeUpdate(sql); }
	@Override public <T> T unwrap(Class<T> iface) throws SQLException { throw unsupported(); }
	@Override public boolean isWrapperFor(Class<?> iface) { return false; }

	private static SQLFeatureNotSupportedException unsupported() {
		return new SQLFeatureNotSupportedException("Not supported by RemoteStatement");
	}
}
