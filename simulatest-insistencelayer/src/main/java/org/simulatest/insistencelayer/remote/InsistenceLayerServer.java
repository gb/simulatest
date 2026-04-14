package org.simulatest.insistencelayer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

import java.util.Objects;

import org.simulatest.insistencelayer.InsistenceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Lightweight TCP server that exposes an {@link InsistenceLayer} for remote control.
 *
 * <p>Accepts one client connection at a time and processes commands sequentially on a single
 * daemon thread. This is correct because savepoints are bound to a single JDBC connection
 * and must not be manipulated concurrently.</p>
 *
 * <p>Embed this in the application process (e.g. during test startup) so that a remote
 * test driver can control the savepoint stack via {@link RemoteInsistenceLayer}.</p>
 *
 * <h3>Usage:</h3>
 * <pre>{@code
 * InsistenceLayerServer server = new InsistenceLayerServer(layer, 4242);
 * server.start();
 * // ... tests run remotely ...
 * server.stop();
 * }</pre>
 *
 * <p><b>Thread-safety:</b> {@link #start()} and {@link #stop()} are intended
 * to be called from a single owning thread. Fields visible to the accept
 * thread are {@code volatile} to guarantee publication of {@code stop()}.</p>
 */
public final class InsistenceLayerServer {

	private static final Logger logger = LoggerFactory.getLogger(InsistenceLayerServer.class);

	private final InsistenceLayer layer;
	private final int requestedPort;
	private volatile ServerSocket serverSocket;
	private volatile Thread serverThread;
	private volatile Socket activeClient;

	/**
	 * Creates a server that will delegate commands to the given layer.
	 *
	 * @param layer the local Insistence Layer (with a real DB connection)
	 * @param port    the TCP port to bind to (use 0 for OS-assigned port)
	 */
	public InsistenceLayerServer(InsistenceLayer layer, int port) {
		this.layer = Objects.requireNonNull(layer, "layer must not be null");
		this.requestedPort = port;
	}

	/**
	 * Starts the server on a daemon thread. Returns immediately.
	 *
	 * @throws IOException if the port cannot be bound
	 */
	public void start() throws IOException {
		serverSocket = new ServerSocket(requestedPort);
		logger.info("Server started on port {}", serverSocket.getLocalPort());

		serverThread = new Thread(this::acceptLoop, "insistence-layer-server");
		serverThread.setDaemon(true);
		serverThread.start();
	}

	/**
	 * Stops the server, closes the listening socket, and waits for the
	 * server thread to terminate (up to 5 seconds).
	 *
	 * @throws IOException if closing the socket fails
	 */
	public void stop() throws IOException {
		logger.info("Stopping server on port {}", requestedPort);
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		closeActiveClient();
		if (serverThread != null) {
			try {
				serverThread.join(5000);
				if (serverThread.isAlive()) {
					logger.warn("Server thread did not terminate within 5 seconds");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warn("Interrupted while waiting for server thread to stop");
			}
		}
	}

	/**
	 * Returns the actual port the server is listening on.
	 * Useful when binding to port 0 (OS-assigned).
	 *
	 * @return the bound port number
	 */
	public int getPort() {
		return serverSocket.getLocalPort();
	}

	private void acceptLoop() {
		while (!serverSocket.isClosed()) {
			try (Socket client = serverSocket.accept()) {
				logger.debug("Client connected from {}", client.getRemoteSocketAddress());
				handleClient(client);
			} catch (SocketException e) {
				if (!serverSocket.isClosed()) {
					logger.error("Accept failed on port {}", serverSocket.getLocalPort(), e);
				}
			} catch (IOException e) {
				logger.error("I/O error on port {}", serverSocket.getLocalPort(), e);
			}
		}
		logger.info("Server stopped on port {}", requestedPort);
	}

	private void handleClient(Socket client) throws IOException {
		activeClient = client;
		try {
			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			while (processNextCommand(client, in, out)) {
			}
		} finally {
			activeClient = null;
		}
	}

	/**
	 * Reads and executes a single command from the client.
	 *
	 * @return {@code true} if the session should continue, {@code false} if the
	 *         client disconnected or the server is shutting down
	 * @throws IOException if a non-shutdown socket error occurs
	 */
	private boolean processNextCommand(Socket client, DataInputStream in, DataOutputStream out) throws IOException {
		if (client.isClosed() || serverSocket.isClosed()) return false;

		try {
			byte command = in.readByte();
			executeCommand(command, out);
			return true;
		} catch (EOFException e) {
			logger.debug("Client {} disconnected", client.getRemoteSocketAddress());
			return false;
		} catch (SocketException e) {
			if (serverSocket.isClosed()) {
				logger.debug("Client connection closed during server shutdown");
				return false;
			}
			throw e;
		}
	}

	private void closeActiveClient() {
		Socket client = activeClient;
		if (client != null && !client.isClosed()) {
			try {
				client.close();
			} catch (IOException e) {
				logger.debug("Error closing active client connection", e);
			}
		}
	}

	private void executeCommand(byte command, DataOutputStream out) throws IOException {
		try {
			dispatch(command);
			InsistenceLayerProtocol.writeOk(out);
		} catch (Exception e) {
			logger.error("Command 0x{} failed at level {}",
				String.format("%02X", command), layer.getCurrentLevel(), e);
			InsistenceLayerProtocol.writeError(out, e.getMessage());
		}
	}

	private void dispatch(byte command) {
		switch (command) {
			case InsistenceLayerProtocol.INCREASE -> layer.increaseLevel();
			case InsistenceLayerProtocol.DECREASE -> layer.decreaseLevel();
			case InsistenceLayerProtocol.RESET    -> layer.resetCurrentLevel();
			default -> throw new IllegalArgumentException("Unknown command: " + command);
		}
	}

}
