package org.simulatest.insistencelayer.server.protocol;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.sql.Types;

public final class SqlTypeMapper {

	private SqlTypeMapper() {
	}

	public static void setParameter(PreparedStatement ps, int index, SqlParameter param) throws SQLException {
		if (param.value() == null) {
			ps.setNull(index, sqlTypeFromName(param.type()));
			return;
		}

		switch (param.type()) {
			case SqlType.STRING -> ps.setString(index, param.value());
			case SqlType.INT -> ps.setInt(index, Integer.parseInt(param.value()));
			case SqlType.LONG -> ps.setLong(index, Long.parseLong(param.value()));
			case SqlType.DOUBLE -> ps.setDouble(index, Double.parseDouble(param.value()));
			case SqlType.FLOAT -> ps.setFloat(index, Float.parseFloat(param.value()));
			case SqlType.BOOLEAN -> ps.setBoolean(index, Boolean.parseBoolean(param.value()));
			case SqlType.BIGDECIMAL -> ps.setBigDecimal(index, new BigDecimal(param.value()));
			case SqlType.DATE -> ps.setDate(index, Date.valueOf(param.value()));
			case SqlType.TIME -> ps.setTime(index, Time.valueOf(param.value()));
			case SqlType.TIMESTAMP -> ps.setTimestamp(index, Timestamp.valueOf(param.value()));
			default -> ps.setString(index, param.value());
		}
	}

	public static String readColumn(ResultSet rs, int index) throws SQLException {
		Object value = rs.getObject(index);
		return value != null ? value.toString() : null;
	}

	private static int sqlTypeFromName(String type) {
		return switch (type) {
			case SqlType.INT -> Types.INTEGER;
			case SqlType.LONG -> Types.BIGINT;
			case SqlType.DOUBLE -> Types.DOUBLE;
			case SqlType.FLOAT -> Types.FLOAT;
			case SqlType.BOOLEAN -> Types.BOOLEAN;
			case SqlType.BIGDECIMAL -> Types.DECIMAL;
			case SqlType.DATE -> Types.DATE;
			case SqlType.TIME -> Types.TIME;
			case SqlType.TIMESTAMP -> Types.TIMESTAMP;
			default -> Types.VARCHAR;
		};
	}
}
