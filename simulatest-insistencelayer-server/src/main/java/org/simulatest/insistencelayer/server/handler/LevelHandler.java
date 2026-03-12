package org.simulatest.insistencelayer.server.handler;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.server.protocol.JsonUtil;

public class LevelHandler implements HttpHandler {

	private final InsistenceLayerManager manager;
	private final Object lock;

	public LevelHandler(InsistenceLayerManager manager, Object lock) {
		this.manager = manager;
		this.lock = lock;
	}

	@Override
	public void handle(HttpExchange exchange) throws IOException {
		try {
			String method = exchange.getRequestMethod();
			String path = exchange.getRequestURI().getPath();

			if ("GET".equals(method) && "/level".equals(path)) {
				respondLevel(exchange);
			} else if ("POST".equals(method)) {
				handlePost(exchange, path);
			} else {
				HandlerSupport.sendError(exchange, 405, "Method not allowed");
			}
		} catch (Exception e) {
			HandlerSupport.sendError(exchange, 500, e.getMessage());
		}
	}

	private void handlePost(HttpExchange exchange, String path) throws IOException {
		synchronized (lock) {
			switch (path) {
				case "/level/increase" -> manager.increaseLevel();
				case "/level/decrease" -> manager.decreaseLevel();
				case "/level/reset" -> manager.resetCurrentLevel();
				default -> { HandlerSupport.sendError(exchange, 404, "Unknown path: " + path); return; }
			}
		}
		respondLevel(exchange);
	}

	private void respondLevel(HttpExchange exchange) throws IOException {
		int level;
		synchronized (lock) {
			level = manager.getCurrentLevel();
		}
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("level", level);
		HandlerSupport.sendJson(exchange, 200, JsonUtil.toJson(body));
	}
}
