package org.simulatest.insistencelayer.server.client;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.List;

import org.simulatest.insistencelayer.server.protocol.ColumnInfo;

public class RemoteResultSetMetaData implements ResultSetMetaData {

	private final List<ColumnInfo> columns;

	public RemoteResultSetMetaData(List<ColumnInfo> columns) {
		this.columns = columns;
	}

	@Override
	public int getColumnCount() {
		return columns.size();
	}

	@Override
	public String getColumnLabel(int column) {
		return columns.get(column - 1).name();
	}

	@Override
	public String getColumnName(int column) {
		return columns.get(column - 1).name();
	}

	@Override
	public int getColumnType(int column) {
		return columns.get(column - 1).sqlType();
	}

	@Override
	public String getColumnTypeName(int column) {
		return columns.get(column - 1).typeName();
	}

	// ── Unsupported ──

	@Override public boolean isAutoIncrement(int column) { return false; }
	@Override public boolean isCaseSensitive(int column) { return true; }
	@Override public boolean isSearchable(int column) { return true; }
	@Override public boolean isCurrency(int column) { return false; }
	@Override public int isNullable(int column) { return columnNullableUnknown; }
	@Override public boolean isSigned(int column) { return false; }
	@Override public int getColumnDisplaySize(int column) { return 0; }
	@Override public String getSchemaName(int column) { return ""; }
	@Override public int getPrecision(int column) { return 0; }
	@Override public int getScale(int column) { return 0; }
	@Override public String getTableName(int column) { return ""; }
	@Override public String getCatalogName(int column) { return ""; }
	@Override public boolean isReadOnly(int column) { return true; }
	@Override public boolean isWritable(int column) { return false; }
	@Override public boolean isDefinitelyWritable(int column) { return false; }
	@Override public String getColumnClassName(int column) { return String.class.getName(); }
	@Override public <T> T unwrap(Class<T> iface) throws SQLException { throw new SQLFeatureNotSupportedException(); }
	@Override public boolean isWrapperFor(Class<?> iface) { return false; }
}
