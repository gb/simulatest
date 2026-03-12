package org.simulatest.insistencelayer.server.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.simulatest.insistencelayer.server.protocol.ColumnInfo;
import org.simulatest.insistencelayer.server.protocol.ErrorResponse;
import org.simulatest.insistencelayer.server.protocol.JsonUtil;
import org.simulatest.insistencelayer.server.protocol.QueryResponse;
import org.simulatest.insistencelayer.server.protocol.SqlRequest;
import org.simulatest.insistencelayer.server.protocol.SqlTypeMapper;

public class SqlQueryHandler implements HttpHandler {

	private final Connection connection;
	private final Object lock;

	public SqlQueryHandler(Connection connection, Object lock) {
		this.connection = connection;
		this.lock = lock;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		if (!"POST".equals(exchange.getRequestMethod())) {
			HandlerSupport.sendError(exchange, 405, "Method not allowed");
			return;
		}

		String body = new String(exchange.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);

		try {
			SqlRequest request = JsonUtil.parseSqlRequest(body);
			QueryResponse response;

			synchronized (lock) {
				try (PreparedStatement ps = connection.prepareStatement(request.sql())) {
					HandlerSupport.setParameters(ps, request.params());
					try (ResultSet rs = ps.executeQuery()) {
						response = buildQueryResponse(rs);
					}
				}
			}

			HandlerSupport.sendJson(exchange, 200, JsonUtil.queryResponseToJson(response));

		} catch (SQLException e) {
			ErrorResponse err = new ErrorResponse(e.getMessage(), e.getSQLState(), e.getErrorCode());
			HandlerSupport.sendJson(exchange, 500, JsonUtil.errorResponseToJson(err));
		}
	}

	private QueryResponse buildQueryResponse(ResultSet rs) throws SQLException {
		ResultSetMetaData meta = rs.getMetaData();
		int colCount = meta.getColumnCount();

		List<ColumnInfo> columns = new ArrayList<>();
		for (int i = 1; i <= colCount; i++) {
			columns.add(new ColumnInfo(meta.getColumnLabel(i), meta.getColumnType(i), meta.getColumnTypeName(i)));
		}

		List<List<String>> rows = new ArrayList<>();
		List<List<Boolean>> wasNull = new ArrayList<>();
		while (rs.next()) {
			List<String> row = new ArrayList<>();
			List<Boolean> nullFlags = new ArrayList<>();
			for (int i = 1; i <= colCount; i++) {
				String value = SqlTypeMapper.readColumn(rs, i);
				row.add(value);
				nullFlags.add(rs.wasNull());
			}
			rows.add(row);
			wasNull.add(nullFlags);
		}

		return new QueryResponse(columns, rows, wasNull);
	}
}
