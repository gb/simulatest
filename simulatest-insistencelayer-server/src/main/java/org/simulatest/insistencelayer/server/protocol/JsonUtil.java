package org.simulatest.insistencelayer.server.protocol;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public final class JsonUtil {

	private JsonUtil() {
	}

	// ── Serialization ──

	public static String toJson(Map<String, Object> map) {
		StringBuilder sb = new StringBuilder("{");
		boolean first = true;
		for (var entry : map.entrySet()) {
			if (!first) sb.append(",");
			first = false;
			sb.append('"').append(escape(entry.getKey())).append("\":");
			sb.append(valueToJson(entry.getValue()));
		}
		sb.append("}");
		return sb.toString();
	}

	@SuppressWarnings("unchecked")
	private static String valueToJson(Object value) {
		if (value == null) return "null";
		if (value instanceof Number || value instanceof Boolean) return value.toString();
		if (value instanceof String s) return '"' + escape(s) + '"';
		if (value instanceof Map<?, ?> m) return toJson((Map<String, Object>) m);
		if (value instanceof List<?> list) {
			StringBuilder sb = new StringBuilder("[");
			for (int i = 0; i < list.size(); i++) {
				if (i > 0) sb.append(",");
				sb.append(valueToJson(list.get(i)));
			}
			sb.append("]");
			return sb.toString();
		}
		return '"' + escape(value.toString()) + '"';
	}

	private static String escape(String s) {
		StringBuilder sb = new StringBuilder(s.length());
		for (int i = 0; i < s.length(); i++) {
			char c = s.charAt(i);
			switch (c) {
				case '"' -> sb.append("\\\"");
				case '\\' -> sb.append("\\\\");
				case '\n' -> sb.append("\\n");
				case '\r' -> sb.append("\\r");
				case '\t' -> sb.append("\\t");
				default -> sb.append(c);
			}
		}
		return sb.toString();
	}

	// ── Parsing ──

	public static Map<String, Object> parseObject(String json) {
		return new JsonParser(json.trim()).readObject();
	}

	// ── Convenience builders ──

	public static String sqlRequestToJson(SqlRequest req) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("sql", req.sql());
		List<Object> paramList = new ArrayList<>();
		for (SqlParameter p : req.params()) {
			Map<String, Object> pm = new LinkedHashMap<>();
			pm.put("type", p.type());
			pm.put("value", p.value());
			paramList.add(pm);
		}
		map.put("params", paramList);
		return toJson(map);
	}

	public static SqlRequest parseSqlRequest(String json) {
		Map<String, Object> map = parseObject(json);
		String sql = (String) map.get("sql");
		List<SqlParameter> params = new ArrayList<>();
		Object rawParams = map.get("params");
		if (rawParams instanceof List<?> list) {
			for (Object item : list) {
				@SuppressWarnings("unchecked")
				Map<String, Object> pm = (Map<String, Object>) item;
				params.add(new SqlParameter((String) pm.get("type"), (String) pm.get("value")));
			}
		}
		return new SqlRequest(sql, params);
	}

	public static String queryResponseToJson(QueryResponse resp) {
		Map<String, Object> map = new LinkedHashMap<>();
		List<Object> cols = new ArrayList<>();
		for (ColumnInfo ci : resp.columns()) {
			Map<String, Object> cm = new LinkedHashMap<>();
			cm.put("name", ci.name());
			cm.put("sqlType", ci.sqlType());
			cm.put("typeName", ci.typeName());
			cols.add(cm);
		}
		map.put("columns", cols);
		map.put("rows", resp.rows());
		map.put("wasNull", resp.wasNull());
		return toJson(map);
	}

	@SuppressWarnings("unchecked")
	public static QueryResponse parseQueryResponse(String json) {
		Map<String, Object> map = parseObject(json);
		List<ColumnInfo> columns = new ArrayList<>();
		for (Object item : (List<?>) map.get("columns")) {
			Map<String, Object> cm = (Map<String, Object>) item;
			columns.add(new ColumnInfo(
				(String) cm.get("name"),
				((Number) cm.get("sqlType")).intValue(),
				(String) cm.get("typeName")
			));
		}
		List<List<String>> rows = new ArrayList<>();
		for (Object row : (List<?>) map.get("rows")) {
			List<String> r = new ArrayList<>();
			for (Object cell : (List<?>) row) {
				r.add(cell != null ? cell.toString() : null);
			}
			rows.add(r);
		}
		List<List<Boolean>> wasNull = new ArrayList<>();
		for (Object row : (List<?>) map.get("wasNull")) {
			List<Boolean> r = new ArrayList<>();
			for (Object cell : (List<?>) row) {
				r.add(Boolean.TRUE.equals(cell));
			}
			wasNull.add(r);
		}
		return new QueryResponse(columns, rows, wasNull);
	}

	public static String errorResponseToJson(ErrorResponse err) {
		Map<String, Object> map = new LinkedHashMap<>();
		map.put("message", err.message());
		map.put("sqlState", err.sqlState());
		map.put("errorCode", err.errorCode());
		return toJson(map);
	}

	public static ErrorResponse parseErrorResponse(String json) {
		Map<String, Object> map = parseObject(json);
		return new ErrorResponse(
			(String) map.get("message"),
			(String) map.get("sqlState"),
			((Number) map.get("errorCode")).intValue()
		);
	}

	// ── Minimal JSON parser ──

	private static class JsonParser {
		private final String input;
		private int pos;

		JsonParser(String input) {
			this.input = input;
			this.pos = 0;
		}

		Map<String, Object> readObject() {
			Map<String, Object> map = new LinkedHashMap<>();
			expect('{');
			skipWhitespace();
			if (peek() == '}') { pos++; return map; }
			while (true) {
				skipWhitespace();
				String key = readString();
				skipWhitespace();
				expect(':');
				skipWhitespace();
				Object value = readValue();
				map.put(key, value);
				skipWhitespace();
				if (peek() == ',') { pos++; continue; }
				break;
			}
			expect('}');
			return map;
		}

		private Object readValue() {
			skipWhitespace();
			char c = peek();
			if (c == '"') return readString();
			if (c == '{') return readObject();
			if (c == '[') return readArray();
			if (c == 'n') { expectLiteral("null"); return null; }
			if (c == 't') { expectLiteral("true"); return Boolean.TRUE; }
			if (c == 'f') { expectLiteral("false"); return Boolean.FALSE; }
			return readNumber();
		}

		private String readString() {
			expect('"');
			StringBuilder sb = new StringBuilder();
			while (pos < input.length()) {
				char c = input.charAt(pos++);
				if (c == '"') return sb.toString();
				if (c == '\\') {
					char next = input.charAt(pos++);
					switch (next) {
						case '"', '\\', '/' -> sb.append(next);
						case 'n' -> sb.append('\n');
						case 'r' -> sb.append('\r');
						case 't' -> sb.append('\t');
						default -> { sb.append('\\'); sb.append(next); }
					}
				} else {
					sb.append(c);
				}
			}
			throw new IllegalArgumentException("Unterminated string");
		}

		private List<Object> readArray() {
			List<Object> list = new ArrayList<>();
			expect('[');
			skipWhitespace();
			if (peek() == ']') { pos++; return list; }
			while (true) {
				skipWhitespace();
				list.add(readValue());
				skipWhitespace();
				if (peek() == ',') { pos++; continue; }
				break;
			}
			expect(']');
			return list;
		}

		private Number readNumber() {
			int start = pos;
			while (pos < input.length() && isNumberChar(input.charAt(pos))) pos++;
			String num = input.substring(start, pos);
			if (num.contains(".") || num.contains("e") || num.contains("E")) {
				return Double.parseDouble(num);
			}
			long v = Long.parseLong(num);
			if (v >= Integer.MIN_VALUE && v <= Integer.MAX_VALUE) return (int) v;
			return v;
		}

		private boolean isNumberChar(char c) {
			return (c >= '0' && c <= '9') || c == '-' || c == '+' || c == '.' || c == 'e' || c == 'E';
		}

		private void expect(char expected) {
			skipWhitespace();
			if (pos >= input.length() || input.charAt(pos) != expected) {
				throw new IllegalArgumentException(
					"Expected '" + expected + "' at position " + pos + " but got: " +
					(pos < input.length() ? "'" + input.charAt(pos) + "'" : "EOF"));
			}
			pos++;
		}

		private void expectLiteral(String literal) {
			for (int i = 0; i < literal.length(); i++) {
				if (pos >= input.length() || input.charAt(pos) != literal.charAt(i)) {
					throw new IllegalArgumentException("Expected '" + literal + "' at position " + pos);
				}
				pos++;
			}
		}

		private char peek() {
			return input.charAt(pos);
		}

		private void skipWhitespace() {
			while (pos < input.length() && Character.isWhitespace(input.charAt(pos))) pos++;
		}
	}
}
