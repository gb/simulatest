package org.simulatest.insistencelayer.server.protocol;

import java.util.List;

public record QueryResponse(List<ColumnInfo> columns, List<List<String>> rows, List<List<Boolean>> wasNull) {

	public QueryResponse {
		columns = columns != null ? List.copyOf(columns) : List.of();
		rows = rows != null ? List.copyOf(rows) : List.of();
		wasNull = wasNull != null ? List.copyOf(wasNull) : List.of();
	}
}
