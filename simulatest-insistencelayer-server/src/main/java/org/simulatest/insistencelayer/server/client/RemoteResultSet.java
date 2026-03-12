package org.simulatest.insistencelayer.server.client;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.*;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.simulatest.insistencelayer.server.protocol.QueryResponse;

public class RemoteResultSet implements ResultSet {

	private final QueryResponse response;
	private final RemoteResultSetMetaData metaData;
	private final Map<String, Integer> columnIndex;
	private int cursor = -1;
	private boolean closed;
	private boolean lastWasNull;

	public RemoteResultSet(QueryResponse response) {
		this.response = response;
		this.metaData = new RemoteResultSetMetaData(response.columns());
		this.columnIndex = new HashMap<>();
		for (int i = 0; i < response.columns().size(); i++) {
			columnIndex.putIfAbsent(response.columns().get(i).name().toUpperCase(), i + 1);
		}
	}

	private String raw(int columnIndex) {
		String value = response.rows().get(cursor).get(columnIndex - 1);
		lastWasNull = response.wasNull().get(cursor).get(columnIndex - 1);
		return value;
	}

	@Override
	public int findColumn(String columnLabel) throws SQLException {
		Integer index = columnIndex.get(columnLabel.toUpperCase());
		if (index == null) throw new SQLException("Column not found: " + columnLabel);
		return index;
	}

	// ── Navigation ──

	@Override public boolean next() { return ++cursor < response.rows().size(); }
	@Override public void close() { closed = true; }
	@Override public boolean isClosed() { return closed; }
	@Override public boolean wasNull() { return lastWasNull; }
	@Override public ResultSetMetaData getMetaData() { return metaData; }

	// ── Getters by index ──

	@Override public String getString(int columnIndex) { return raw(columnIndex); }
	@Override public boolean getBoolean(int columnIndex) { String v = raw(columnIndex); return v != null && Boolean.parseBoolean(v); }
	@Override public byte getByte(int columnIndex) { String v = raw(columnIndex); return v != null ? Byte.parseByte(v) : 0; }
	@Override public short getShort(int columnIndex) { String v = raw(columnIndex); return v != null ? Short.parseShort(v) : 0; }
	@Override public int getInt(int columnIndex) { String v = raw(columnIndex); return v != null ? Integer.parseInt(v) : 0; }
	@Override public long getLong(int columnIndex) { String v = raw(columnIndex); return v != null ? Long.parseLong(v) : 0; }
	@Override public float getFloat(int columnIndex) { String v = raw(columnIndex); return v != null ? Float.parseFloat(v) : 0; }
	@Override public double getDouble(int columnIndex) { String v = raw(columnIndex); return v != null ? Double.parseDouble(v) : 0; }
	@Override public BigDecimal getBigDecimal(int columnIndex) { String v = raw(columnIndex); return v != null ? new BigDecimal(v) : null; }
	@Override public Object getObject(int columnIndex) { return raw(columnIndex); }

	// ── Getters by label ──

	@Override public String getString(String columnLabel) throws SQLException { return getString(findColumn(columnLabel)); }
	@Override public boolean getBoolean(String columnLabel) throws SQLException { return getBoolean(findColumn(columnLabel)); }
	@Override public byte getByte(String columnLabel) throws SQLException { return getByte(findColumn(columnLabel)); }
	@Override public short getShort(String columnLabel) throws SQLException { return getShort(findColumn(columnLabel)); }
	@Override public int getInt(String columnLabel) throws SQLException { return getInt(findColumn(columnLabel)); }
	@Override public long getLong(String columnLabel) throws SQLException { return getLong(findColumn(columnLabel)); }
	@Override public float getFloat(String columnLabel) throws SQLException { return getFloat(findColumn(columnLabel)); }
	@Override public double getDouble(String columnLabel) throws SQLException { return getDouble(findColumn(columnLabel)); }
	@Override public BigDecimal getBigDecimal(String columnLabel) throws SQLException { return getBigDecimal(findColumn(columnLabel)); }
	@Override public Object getObject(String columnLabel) throws SQLException { return getObject(findColumn(columnLabel)); }

	// ── Positioning ──

	@Override public boolean isBeforeFirst() { return cursor < 0; }
	@Override public boolean isAfterLast() { return cursor >= response.rows().size(); }
	@Override public boolean isFirst() { return cursor == 0; }
	@Override public boolean isLast() { return cursor == response.rows().size() - 1; }
	@Override public int getRow() { return cursor >= 0 && cursor < response.rows().size() ? cursor + 1 : 0; }

	// ── Unsupported (throw SQLFeatureNotSupportedException) ──

	@Override public void beforeFirst() throws SQLException { throw unsupported(); }
	@Override public void afterLast() throws SQLException { throw unsupported(); }
	@Override public boolean first() throws SQLException { throw unsupported(); }
	@Override public boolean last() throws SQLException { throw unsupported(); }
	@Override public boolean absolute(int row) throws SQLException { throw unsupported(); }
	@Override public boolean relative(int rows) throws SQLException { throw unsupported(); }
	@Override public boolean previous() throws SQLException { throw unsupported(); }

	@Override public int getType() { return TYPE_FORWARD_ONLY; }
	@Override public int getConcurrency() { return CONCUR_READ_ONLY; }
	@Override public int getFetchDirection() { return FETCH_FORWARD; }
	@Override public void setFetchDirection(int direction) {}
	@Override public int getFetchSize() { return 0; }
	@Override public void setFetchSize(int rows) {}
	@Override public Statement getStatement() { return null; }
	@Override public SQLWarning getWarnings() { return null; }
	@Override public void clearWarnings() {}
	@Override public String getCursorName() throws SQLException { throw unsupported(); }
	@Override public int getHoldability() { return CLOSE_CURSORS_AT_COMMIT; }

	// ── Deprecated / rarely-used getters ──

	@SuppressWarnings("deprecation")
	@Override public BigDecimal getBigDecimal(int columnIndex, int scale) throws SQLException { throw unsupported(); }
	@SuppressWarnings("deprecation")
	@Override public BigDecimal getBigDecimal(String columnLabel, int scale) throws SQLException { throw unsupported(); }
	@Override public byte[] getBytes(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public byte[] getBytes(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Date getDate(int columnIndex) throws SQLException { String v = raw(columnIndex); return v != null ? Date.valueOf(v) : null; }
	@Override public Date getDate(String columnLabel) throws SQLException { return getDate(findColumn(columnLabel)); }
	@Override public Time getTime(int columnIndex) throws SQLException { String v = raw(columnIndex); return v != null ? Time.valueOf(v) : null; }
	@Override public Time getTime(String columnLabel) throws SQLException { return getTime(findColumn(columnLabel)); }
	@Override public Timestamp getTimestamp(int columnIndex) throws SQLException { String v = raw(columnIndex); return v != null ? Timestamp.valueOf(v) : null; }
	@Override public Timestamp getTimestamp(String columnLabel) throws SQLException { return getTimestamp(findColumn(columnLabel)); }
	@Override public InputStream getAsciiStream(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public InputStream getAsciiStream(String columnLabel) throws SQLException { throw unsupported(); }
	@SuppressWarnings("deprecation")
	@Override public InputStream getUnicodeStream(int columnIndex) throws SQLException { throw unsupported(); }
	@SuppressWarnings("deprecation")
	@Override public InputStream getUnicodeStream(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public InputStream getBinaryStream(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public InputStream getBinaryStream(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Reader getCharacterStream(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public Reader getCharacterStream(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Object getObject(int columnIndex, Map<String, Class<?>> map) throws SQLException { throw unsupported(); }
	@Override public Object getObject(String columnLabel, Map<String, Class<?>> map) throws SQLException { throw unsupported(); }
	@Override public <T> T getObject(int columnIndex, Class<T> type) throws SQLException { throw unsupported(); }
	@Override public <T> T getObject(String columnLabel, Class<T> type) throws SQLException { throw unsupported(); }
	@Override public Ref getRef(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public Ref getRef(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Blob getBlob(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public Blob getBlob(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Clob getClob(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public Clob getClob(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Array getArray(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public Array getArray(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public URL getURL(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public URL getURL(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Date getDate(int columnIndex, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public Date getDate(String columnLabel, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public Time getTime(int columnIndex, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public Time getTime(String columnLabel, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public Timestamp getTimestamp(int columnIndex, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public Timestamp getTimestamp(String columnLabel, Calendar cal) throws SQLException { throw unsupported(); }
	@Override public NClob getNClob(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public NClob getNClob(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public SQLXML getSQLXML(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public SQLXML getSQLXML(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public String getNString(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public String getNString(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public Reader getNCharacterStream(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public Reader getNCharacterStream(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public RowId getRowId(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public RowId getRowId(String columnLabel) throws SQLException { throw unsupported(); }

	// ── Updates (all unsupported) ──

	@Override public boolean rowUpdated() { return false; }
	@Override public boolean rowInserted() { return false; }
	@Override public boolean rowDeleted() { return false; }
	@Override public void updateNull(int columnIndex) throws SQLException { throw unsupported(); }
	@Override public void updateBoolean(int columnIndex, boolean x) throws SQLException { throw unsupported(); }
	@Override public void updateByte(int columnIndex, byte x) throws SQLException { throw unsupported(); }
	@Override public void updateShort(int columnIndex, short x) throws SQLException { throw unsupported(); }
	@Override public void updateInt(int columnIndex, int x) throws SQLException { throw unsupported(); }
	@Override public void updateLong(int columnIndex, long x) throws SQLException { throw unsupported(); }
	@Override public void updateFloat(int columnIndex, float x) throws SQLException { throw unsupported(); }
	@Override public void updateDouble(int columnIndex, double x) throws SQLException { throw unsupported(); }
	@Override public void updateBigDecimal(int columnIndex, BigDecimal x) throws SQLException { throw unsupported(); }
	@Override public void updateString(int columnIndex, String x) throws SQLException { throw unsupported(); }
	@Override public void updateBytes(int columnIndex, byte[] x) throws SQLException { throw unsupported(); }
	@Override public void updateDate(int columnIndex, Date x) throws SQLException { throw unsupported(); }
	@Override public void updateTime(int columnIndex, Time x) throws SQLException { throw unsupported(); }
	@Override public void updateTimestamp(int columnIndex, Timestamp x) throws SQLException { throw unsupported(); }
	@Override public void updateAsciiStream(int columnIndex, InputStream x, int length) throws SQLException { throw unsupported(); }
	@Override public void updateBinaryStream(int columnIndex, InputStream x, int length) throws SQLException { throw unsupported(); }
	@Override public void updateCharacterStream(int columnIndex, Reader x, int length) throws SQLException { throw unsupported(); }
	@Override public void updateObject(int columnIndex, Object x, int scaleOrLength) throws SQLException { throw unsupported(); }
	@Override public void updateObject(int columnIndex, Object x) throws SQLException { throw unsupported(); }
	@Override public void updateNull(String columnLabel) throws SQLException { throw unsupported(); }
	@Override public void updateBoolean(String columnLabel, boolean x) throws SQLException { throw unsupported(); }
	@Override public void updateByte(String columnLabel, byte x) throws SQLException { throw unsupported(); }
	@Override public void updateShort(String columnLabel, short x) throws SQLException { throw unsupported(); }
	@Override public void updateInt(String columnLabel, int x) throws SQLException { throw unsupported(); }
	@Override public void updateLong(String columnLabel, long x) throws SQLException { throw unsupported(); }
	@Override public void updateFloat(String columnLabel, float x) throws SQLException { throw unsupported(); }
	@Override public void updateDouble(String columnLabel, double x) throws SQLException { throw unsupported(); }
	@Override public void updateBigDecimal(String columnLabel, BigDecimal x) throws SQLException { throw unsupported(); }
	@Override public void updateString(String columnLabel, String x) throws SQLException { throw unsupported(); }
	@Override public void updateBytes(String columnLabel, byte[] x) throws SQLException { throw unsupported(); }
	@Override public void updateDate(String columnLabel, Date x) throws SQLException { throw unsupported(); }
	@Override public void updateTime(String columnLabel, Time x) throws SQLException { throw unsupported(); }
	@Override public void updateTimestamp(String columnLabel, Timestamp x) throws SQLException { throw unsupported(); }
	@Override public void updateAsciiStream(String columnLabel, InputStream x, int length) throws SQLException { throw unsupported(); }
	@Override public void updateBinaryStream(String columnLabel, InputStream x, int length) throws SQLException { throw unsupported(); }
	@Override public void updateCharacterStream(String columnLabel, Reader x, int length) throws SQLException { throw unsupported(); }
	@Override public void updateObject(String columnLabel, Object x, int scaleOrLength) throws SQLException { throw unsupported(); }
	@Override public void updateObject(String columnLabel, Object x) throws SQLException { throw unsupported(); }
	@Override public void insertRow() throws SQLException { throw unsupported(); }
	@Override public void updateRow() throws SQLException { throw unsupported(); }
	@Override public void deleteRow() throws SQLException { throw unsupported(); }
	@Override public void refreshRow() throws SQLException { throw unsupported(); }
	@Override public void cancelRowUpdates() throws SQLException { throw unsupported(); }
	@Override public void moveToInsertRow() throws SQLException { throw unsupported(); }
	@Override public void moveToCurrentRow() throws SQLException { throw unsupported(); }
	@Override public void updateRef(int columnIndex, Ref x) throws SQLException { throw unsupported(); }
	@Override public void updateRef(String columnLabel, Ref x) throws SQLException { throw unsupported(); }
	@Override public void updateBlob(int columnIndex, Blob x) throws SQLException { throw unsupported(); }
	@Override public void updateBlob(String columnLabel, Blob x) throws SQLException { throw unsupported(); }
	@Override public void updateClob(int columnIndex, Clob x) throws SQLException { throw unsupported(); }
	@Override public void updateClob(String columnLabel, Clob x) throws SQLException { throw unsupported(); }
	@Override public void updateArray(int columnIndex, Array x) throws SQLException { throw unsupported(); }
	@Override public void updateArray(String columnLabel, Array x) throws SQLException { throw unsupported(); }
	@Override public void updateRowId(int columnIndex, RowId x) throws SQLException { throw unsupported(); }
	@Override public void updateRowId(String columnLabel, RowId x) throws SQLException { throw unsupported(); }
	@Override public void updateNString(int columnIndex, String nString) throws SQLException { throw unsupported(); }
	@Override public void updateNString(String columnLabel, String nString) throws SQLException { throw unsupported(); }
	@Override public void updateNClob(int columnIndex, NClob nClob) throws SQLException { throw unsupported(); }
	@Override public void updateNClob(String columnLabel, NClob nClob) throws SQLException { throw unsupported(); }
	@Override public void updateSQLXML(int columnIndex, SQLXML xmlObject) throws SQLException { throw unsupported(); }
	@Override public void updateSQLXML(String columnLabel, SQLXML xmlObject) throws SQLException { throw unsupported(); }
	@Override public void updateNCharacterStream(int columnIndex, Reader x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateNCharacterStream(String columnLabel, Reader x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateAsciiStream(int columnIndex, InputStream x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateAsciiStream(String columnLabel, InputStream x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateBinaryStream(int columnIndex, InputStream x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateBinaryStream(String columnLabel, InputStream x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateCharacterStream(int columnIndex, Reader x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateCharacterStream(String columnLabel, Reader x, long length) throws SQLException { throw unsupported(); }
	@Override public void updateBlob(int columnIndex, InputStream inputStream, long length) throws SQLException { throw unsupported(); }
	@Override public void updateBlob(String columnLabel, InputStream inputStream, long length) throws SQLException { throw unsupported(); }
	@Override public void updateClob(int columnIndex, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void updateClob(String columnLabel, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void updateNClob(int columnIndex, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void updateNClob(String columnLabel, Reader reader, long length) throws SQLException { throw unsupported(); }
	@Override public void updateNCharacterStream(int columnIndex, Reader x) throws SQLException { throw unsupported(); }
	@Override public void updateNCharacterStream(String columnLabel, Reader x) throws SQLException { throw unsupported(); }
	@Override public void updateAsciiStream(int columnIndex, InputStream x) throws SQLException { throw unsupported(); }
	@Override public void updateAsciiStream(String columnLabel, InputStream x) throws SQLException { throw unsupported(); }
	@Override public void updateBinaryStream(int columnIndex, InputStream x) throws SQLException { throw unsupported(); }
	@Override public void updateBinaryStream(String columnLabel, InputStream x) throws SQLException { throw unsupported(); }
	@Override public void updateCharacterStream(int columnIndex, Reader x) throws SQLException { throw unsupported(); }
	@Override public void updateCharacterStream(String columnLabel, Reader x) throws SQLException { throw unsupported(); }
	@Override public void updateBlob(int columnIndex, InputStream inputStream) throws SQLException { throw unsupported(); }
	@Override public void updateBlob(String columnLabel, InputStream inputStream) throws SQLException { throw unsupported(); }
	@Override public void updateClob(int columnIndex, Reader reader) throws SQLException { throw unsupported(); }
	@Override public void updateClob(String columnLabel, Reader reader) throws SQLException { throw unsupported(); }
	@Override public void updateNClob(int columnIndex, Reader reader) throws SQLException { throw unsupported(); }
	@Override public void updateNClob(String columnLabel, Reader reader) throws SQLException { throw unsupported(); }

	// ── Wrapper ──

	@Override public <T> T unwrap(Class<T> iface) throws SQLException { throw unsupported(); }
	@Override public boolean isWrapperFor(Class<?> iface) { return false; }

	private static SQLFeatureNotSupportedException unsupported() {
		return new SQLFeatureNotSupportedException("Not supported by RemoteResultSet");
	}
}
