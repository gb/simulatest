package org.simulatest.insistencelayer.server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.sql.Connection;
import java.util.Objects;

import com.sun.net.httpserver.HttpServer;

import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.server.handler.LevelHandler;
import org.simulatest.insistencelayer.server.handler.SqlExecuteHandler;
import org.simulatest.insistencelayer.server.handler.SqlQueryHandler;
import org.simulatest.insistencelayer.server.infra.InsistenceLayerServerException;

public class InsistenceLayerServer {

	private final int port;
	private final Connection connection;
	private final Object lock = new Object();
	private HttpServer httpServer;

	public InsistenceLayerServer(int port, Connection connection) {
		this.port = port;
		this.connection = Objects.requireNonNull(connection, "Connection must not be null");
	}

	public void start() {
		if (httpServer != null) {
			throw new InsistenceLayerServerException("Server is already running");
		}

		ConnectionWrapper wrapper = new ConnectionWrapper(connection);
		InsistenceLayerManager manager = InsistenceLayerManagerFactory.build(wrapper);

		try {
			httpServer = HttpServer.create(new InetSocketAddress(port), 0);
		} catch (IOException e) {
			throw new InsistenceLayerServerException("Failed to start HTTP server on port " + port, e);
		}

		httpServer.createContext("/level", new LevelHandler(manager, lock));
		httpServer.createContext("/sql/execute", new SqlExecuteHandler(wrapper.getConnection(), lock));
		httpServer.createContext("/sql/query", new SqlQueryHandler(wrapper.getConnection(), lock));
		httpServer.setExecutor(null);
		httpServer.start();
	}

	public void stop() {
		if (httpServer != null) {
			httpServer.stop(0);
			httpServer = null;
		}
	}

	public int getPort() {
		if (httpServer != null) {
			return httpServer.getAddress().getPort();
		}
		return port;
	}

	public boolean isRunning() {
		return httpServer != null;
	}
}
