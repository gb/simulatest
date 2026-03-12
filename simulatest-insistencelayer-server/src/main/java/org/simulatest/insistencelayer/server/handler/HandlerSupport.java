package org.simulatest.insistencelayer.server.handler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;

import org.simulatest.insistencelayer.server.protocol.JsonUtil;
import org.simulatest.insistencelayer.server.protocol.SqlParameter;
import org.simulatest.insistencelayer.server.protocol.SqlTypeMapper;

final class HandlerSupport {

	private HandlerSupport() {
	}

	static void sendJson(HttpExchange exchange, int status, String json) throws IOException {
		byte[] bytes = json.getBytes(StandardCharsets.UTF_8);
		exchange.getResponseHeaders().set("Content-Type", "application/json");
		exchange.sendResponseHeaders(status, bytes.length);
		try (OutputStream os = exchange.getResponseBody()) {
			os.write(bytes);
		}
	}

	static void sendError(HttpExchange exchange, int status, String message) throws IOException {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("error", message != null ? message : "Unknown error");
		sendJson(exchange, status, JsonUtil.toJson(body));
	}

	static void setParameters(PreparedStatement ps, List<SqlParameter> params) throws SQLException {
		for (int i = 0; i < params.size(); i++) {
			SqlTypeMapper.setParameter(ps, i + 1, params.get(i));
		}
	}
}
