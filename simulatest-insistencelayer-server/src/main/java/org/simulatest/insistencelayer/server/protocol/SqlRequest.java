package org.simulatest.insistencelayer.server.protocol;

import java.util.List;

public record SqlRequest(String sql, List<SqlParameter> params) {

	public SqlRequest {
		if (sql == null || sql.isBlank()) {
			throw new IllegalArgumentException("SQL must not be blank");
		}
		params = params != null ? List.copyOf(params) : List.of();
	}
}
