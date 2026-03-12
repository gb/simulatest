package org.simulatest.insistencelayer.server.protocol;

import static org.junit.Assert.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;

public class JsonUtilTest {

	@Test
	public void shouldRoundTripSimpleObject() {
		Map<String, Object> original = new LinkedHashMap<>();
		original.put("name", "test");
		original.put("count", 42);
		original.put("active", true);

		String json = JsonUtil.toJson(original);
		Map<String, Object> parsed = JsonUtil.parseObject(json);

		assertEquals("test", parsed.get("name"));
		assertEquals(42, parsed.get("count"));
		assertEquals(true, parsed.get("active"));
	}

	@Test
	public void shouldHandleNullValues() {
		Map<String, Object> original = new LinkedHashMap<>();
		original.put("key", null);

		String json = JsonUtil.toJson(original);
		assertTrue(json.contains("null"));

		Map<String, Object> parsed = JsonUtil.parseObject(json);
		assertNull(parsed.get("key"));
	}

	@Test
	public void shouldHandleNestedArrays() {
		Map<String, Object> original = new LinkedHashMap<>();
		original.put("items", List.of("a", "b", "c"));

		String json = JsonUtil.toJson(original);
		Map<String, Object> parsed = JsonUtil.parseObject(json);

		@SuppressWarnings("unchecked")
		List<Object> items = (List<Object>) parsed.get("items");
		assertEquals(3, items.size());
		assertEquals("a", items.get(0));
	}

	@Test
	public void shouldEscapeSpecialCharacters() {
		Map<String, Object> original = new LinkedHashMap<>();
		original.put("text", "line1\nline2\ttab\"quote\\backslash");

		String json = JsonUtil.toJson(original);
		Map<String, Object> parsed = JsonUtil.parseObject(json);

		assertEquals("line1\nline2\ttab\"quote\\backslash", parsed.get("text"));
	}

	@Test
	public void shouldRoundTripSqlRequest() {
		SqlRequest request = new SqlRequest("SELECT * FROM t WHERE id = ?",
			List.of(new SqlParameter("INT", "42")));

		String json = JsonUtil.sqlRequestToJson(request);
		SqlRequest parsed = JsonUtil.parseSqlRequest(json);

		assertEquals("SELECT * FROM t WHERE id = ?", parsed.sql());
		assertEquals(1, parsed.params().size());
		assertEquals("INT", parsed.params().get(0).type());
		assertEquals("42", parsed.params().get(0).value());
	}

	@Test
	public void shouldRoundTripQueryResponse() {
		QueryResponse response = new QueryResponse(
			List.of(new ColumnInfo("ID", 4, "INTEGER"), new ColumnInfo("NAME", 12, "VARCHAR")),
			List.of(List.of("1", "Alice"), List.of("2", "Bob")),
			List.of(List.of(false, false), List.of(false, false))
		);

		String json = JsonUtil.queryResponseToJson(response);
		QueryResponse parsed = JsonUtil.parseQueryResponse(json);

		assertEquals(2, parsed.columns().size());
		assertEquals("ID", parsed.columns().get(0).name());
		assertEquals(2, parsed.rows().size());
		assertEquals("Alice", parsed.rows().get(0).get(1));
	}

	@Test
	public void shouldRoundTripErrorResponse() {
		ErrorResponse error = new ErrorResponse("Table not found", "42S02", 42102);

		String json = JsonUtil.errorResponseToJson(error);
		ErrorResponse parsed = JsonUtil.parseErrorResponse(json);

		assertEquals("Table not found", parsed.message());
		assertEquals("42S02", parsed.sqlState());
		assertEquals(42102, parsed.errorCode());
	}

	@Test
	public void shouldHandleEmptyObject() {
		Map<String, Object> parsed = JsonUtil.parseObject("{}");
		assertTrue(parsed.isEmpty());
	}

	@Test
	public void shouldHandleEmptyArray() {
		Map<String, Object> original = new LinkedHashMap<>();
		original.put("empty", List.of());

		String json = JsonUtil.toJson(original);
		Map<String, Object> parsed = JsonUtil.parseObject(json);

		@SuppressWarnings("unchecked")
		List<Object> items = (List<Object>) parsed.get("empty");
		assertTrue(items.isEmpty());
	}
}
