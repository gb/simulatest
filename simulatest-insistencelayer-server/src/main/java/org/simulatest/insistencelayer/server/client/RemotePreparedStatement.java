package org.simulatest.insistencelayer.server.client;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.net.http.HttpClient;
import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import org.simulatest.insistencelayer.server.protocol.QueryResponse;
import org.simulatest.insistencelayer.server.protocol.SqlParameter;
import org.simulatest.insistencelayer.server.protocol.SqlRequest;
import org.simulatest.insistencelayer.server.protocol.SqlType;

public class RemotePreparedStatement extends RemoteStatement implements PreparedStatement {

	private final String sql;
	private final List<SqlParameter> params = new ArrayList<>();

	public RemotePreparedStatement(HttpClient httpClient, String baseUrl, String sql) {
		super(httpClient, baseUrl);
		this.sql = sql;
	}

	private void setParam(int index, String type, String value) {
		while (params.size() < index) params.add(null);
		params.set(index - 1, new SqlParameter(type, value));
	}

	@Override
	public ResultSet executeQuery() throws SQLException {
		SqlRequest request = new SqlRequest(sql, List.copyOf(params));
		QueryResponse response = sendQuery(request);
		RemoteResultSet rs = new RemoteResultSet(response);
		params.clear();
		return rs;
	}

	@Override
	public int executeUpdate() throws SQLException {
		SqlRequest request = new SqlRequest(sql, List.copyOf(params));
		int count = sendExecute(request);
		params.clear();
		return count;
	}

	@Override
	public boolean execute() throws SQLException {
		if (isQuery(sql)) {
			executeQuery();
			return true;
		}
		executeUpdate();
		return false;
	}

	@Override public void clearParameters() { params.clear(); }

	// ── Setters ──

	@Override public void setNull(int parameterIndex, int sqlType) { setParam(parameterIndex, SqlType.STRING, null); }
	@Override public void setBoolean(int parameterIndex, boolean x) { setParam(parameterIndex, SqlType.BOOLEAN, String.valueOf(x)); }
	@Override public void setByte(int parameterIndex, byte x) { setParam(parameterIndex, SqlType.INT, String.valueOf(x)); }
	@Override public void setShort(int parameterIndex, short x) { setParam(parameterIndex, SqlType.INT, String.valueOf(x)); }
	@Override public void setInt(int parameterIndex, int x) { setParam(parameterIndex, SqlType.INT, String.valueOf(x)); }
	@Override public void setLong(int parameterIndex, long x) { setParam(parameterIndex, SqlType.LONG, String.valueOf(x)); }
	@Override public void setFloat(int parameterIndex, float x) { setParam(parameterIndex, SqlType.FLOAT, String.valueOf(x)); }
	@Override public void setDouble(int parameterIndex, double x) { setParam(parameterIndex, SqlType.DOUBLE, String.valueOf(x)); }
	@Override public void setBigDecimal(int parameterIndex, BigDecimal x) { setParam(parameterIndex, SqlType.BIGDECIMAL, x != null ? x.toString() : null); }
	@Override public void setString(int parameterIndex, String x) { setParam(parameterIndex, SqlType.STRING, x); }
	@Override public void setDate(int parameterIndex, Date x) { setParam(parameterIndex, SqlType.DATE, x != null ? x.toString() : null); }
	@Override public void setTime(int parameterIndex, Time x) { setParam(parameterIndex, SqlType.TIME, x != null ? x.toString() : null); }
	@Override public void setTimestamp(int parameterIndex, Timestamp x) { setParam(parameterIndex, SqlType.TIMESTAMP, x != null ? x.toString() : null); }
	@Override public void setObject(int parameterIndex, Object x) { setParam(parameterIndex, SqlType.STRING, x != null ? x.toString() : null); }
	@Override public void setObject(int parameterIndex, Object x, int targetSqlType) { setObject(parameterIndex, x); }
	@Override public void setObject(int parameterIndex, Object x, int targetSqlType, int scaleOrLength) { setObject(parameterIndex, x); }
	@Override public void setNull(int parameterIndex, int sqlType, String typeName) { setNull(parameterIndex, sqlType); }

	// ── Metadata ──

	@Override public ResultSetMetaData getMetaData() { return null; }
	@Override public ParameterMetaData getParameterMetaData() throws SQLException { throw unsupported(); }

	// ── Batch ──

	@Override public void addBatch() throws SQLException { throw unsupported(); }

	// ── Unsupported setters ──

	@Override public void setBytes(int parameterIndex, byte[] x) throws SQLException { throw unsupported(); }
	@Override public void setAsciiStream(int parameterIndex, InputStream x, int length) throws SQLException { throw unsupported(); }
	@SuppressWarnings("deprecation")
	@Override public void setUnicodeStream(int parameterIndex, InputStream x, int length) throws SQLException { throw unsupported(); }
	@Override public void setBinaryStream(int parameterIndex, InputStream x, int length) throws SQLException { throw unsupported(); }
	@Override public void setCharacterStream(int parameterIndex, Reader reader, int length) throws SQLException { throw unsupported(); }
	@Override public void setRef(int parameterIndex, Ref x) throws SQLException { throw unsupported(); }
	@Override public void setBlob(int parameterIndex, Blob x) throws SQLException { throw unsupported(); }
	@Override public void setClob(int parameterIndex, Clob x) throws SQLException { throw unsupported(); }
	@Override public void setArray(int parameterIndex, Array x) throws SQLException { throw unsupported(); }
	@Override public void setDate(int parameterIndex, Date x, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public void setTime(int parameterIndex, Time x, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public void setTimestamp(int parameterIndex, Timestamp x, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public void setURL(int parameterIndex, URL x) throws SQLException { throw unsupported(); }
	@Override public void setRowId(int parameterIndex, RowId x) throws SQLException { throw unsupported(); }
	@Override public void setNString(int parameterIndex, String value) throws SQLException { throw unsupported(); }
	@Override public void setNCharacterStream(int parameterIndex, Reader value, long length) throws SQLException { throw unsupported(); }
	@Override public void setNClob(int parameterIndex, NClob value) throws SQLException { throw unsupported(); }
	@Override public void setClob(int parameterIndex, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void setBlob(int parameterIndex, InputStream inputStream, long length) throws SQLException { throw unsupported(); }
	@Override public void setNClob(int parameterIndex, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void setSQLXML(int parameterIndex, SQLXML xmlObject) throws SQLException { throw unsupported(); }
	@Override public void setAsciiStream(int parameterIndex, InputStream x, long length) throws SQLException { throw unsupported(); }
	@Override public void setBinaryStream(int parameterIndex, InputStream x, long length) throws SQLException { throw unsupported(); }
	@Override public void setCharacterStream(int parameterIndex, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void setAsciiStream(int parameterIndex, InputStream x) throws SQLException { throw unsupported(); }
	@Override public void setBinaryStream(int parameterIndex, InputStream x) throws SQLException { throw unsupported(); }
	@Override public void setCharacterStream(int parameterIndex, Reader reader) throws SQLException { throw unsupported(); }
	@Override public void setNCharacterStream(int parameterIndex, Reader value) throws SQLException { throw unsupported(); }
	@Override public void setClob(int parameterIndex, Reader reader) throws SQLException { throw unsupported(); }
	@Override public void setBlob(int parameterIndex, InputStream inputStream) throws SQLException { throw unsupported(); }
	@Override public void setNClob(int parameterIndex, Reader reader) throws SQLException { throw unsupported(); }

	private static SQLFeatureNotSupportedException unsupported() {
		return new SQLFeatureNotSupportedException("Not supported by RemotePreparedStatement");
	}
}
