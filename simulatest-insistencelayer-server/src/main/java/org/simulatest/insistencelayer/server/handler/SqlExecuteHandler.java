package org.simulatest.insistencelayer.server.handler;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.simulatest.insistencelayer.server.protocol.ErrorResponse;
import org.simulatest.insistencelayer.server.protocol.JsonUtil;
import org.simulatest.insistencelayer.server.protocol.SqlRequest;

public class SqlExecuteHandler implements HttpHandler {

	private final Connection connection;
	private final Object lock;

	public SqlExecuteHandler(Connection connection, Object lock) {
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
			int updateCount;

			synchronized (lock) {
				try (PreparedStatement ps = connection.prepareStatement(request.sql())) {
					HandlerSupport.setParameters(ps, request.params());
					updateCount = ps.executeUpdate();
				}
			}

			Map<String, Object> response = new LinkedHashMap<>();
			response.put("updateCount", updateCount);
			HandlerSupport.sendJson(exchange, 200, JsonUtil.toJson(response));

		} catch (SQLException e) {
			ErrorResponse err = new ErrorResponse(e.getMessage(), e.getSQLState(), e.getErrorCode());
			HandlerSupport.sendJson(exchange, 500, JsonUtil.errorResponseToJson(err));
		}
	}
}
