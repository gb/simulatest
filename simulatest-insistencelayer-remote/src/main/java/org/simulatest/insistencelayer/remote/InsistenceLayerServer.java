package org.simulatest.insistencelayer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.nio.charset.StandardCharsets;

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
 * InsistenceLayerServer server = new InsistenceLayerServer(manager, 4242);
 * server.start();
 * // ... tests run remotely ...
 * server.stop();
 * }</pre>
 */
public class InsistenceLayerServer {

	private static final Logger logger = LoggerFactory.getLogger(InsistenceLayerServer.class);

	private final InsistenceLayer manager;
	private final int requestedPort;
	private ServerSocket serverSocket;
	private Thread serverThread;
	private volatile Socket activeClient;

	/**
	 * Creates a server that will delegate commands to the given manager.
	 *
	 * @param manager the local Insistence Layer manager (with a real DB connection)
	 * @param port    the TCP port to bind to (use 0 for OS-assigned port)
	 */
	public InsistenceLayerServer(InsistenceLayer manager, int port) {
		this.manager = manager;
		this.requestedPort = port;
	}

	/**
	 * Starts the server on a daemon thread. Returns immediately.
	 *
	 * @throws IOException if the port cannot be bound
	 */
	public void start() throws IOException {
		serverSocket = new ServerSocket(requestedPort);
		logger.info("[InsistenceLayer Remote] Server started on port {}", serverSocket.getLocalPort());

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
		logger.info("[InsistenceLayer Remote] Stopping server on port {}", requestedPort);
		if (serverSocket != null && !serverSocket.isClosed()) {
			serverSocket.close();
		}
		closeActiveClient();
		if (serverThread != null) {
			try {
				serverThread.join(5000);
				if (serverThread.isAlive()) {
					logger.warn("[InsistenceLayer Remote] Server thread did not terminate within 5 seconds");
				}
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				logger.warn("[InsistenceLayer Remote] Interrupted while waiting for server thread to stop");
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
				logger.info("[InsistenceLayer Remote] Client connected from {}", client.getRemoteSocketAddress());
				handleClient(client);
			} catch (SocketException e) {
				if (!serverSocket.isClosed()) {
					logger.error("[InsistenceLayer Remote] Accept failed on port {}", serverSocket.getLocalPort(), e);
				}
			} catch (IOException e) {
				logger.error("[InsistenceLayer Remote] I/O error on port {}", serverSocket.getLocalPort(), e);
			}
		}
		logger.info("[InsistenceLayer Remote] Server stopped on port {}", requestedPort);
	}

	private void handleClient(Socket client) throws IOException {
		activeClient = client;
		try {
			DataInputStream in = new DataInputStream(client.getInputStream());
			DataOutputStream out = new DataOutputStream(client.getOutputStream());

			while (processNextCommand(client, in, out)) {
				// each iteration reads and executes one command
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
			logger.info("[InsistenceLayer Remote] Client {} disconnected", client.getRemoteSocketAddress());
			return false;
		} catch (SocketException e) {
			if (serverSocket.isClosed()) {
				logger.info("[InsistenceLayer Remote] Client connection closed during server shutdown");
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
				logger.debug("[InsistenceLayer Remote] Error closing active client connection", e);
			}
		}
	}

	private void executeCommand(byte command, DataOutputStream out) throws IOException {
		try {
			dispatch(command);
			sendOk(out);
		} catch (Exception e) {
			logger.error("[InsistenceLayer Remote] Command 0x{} failed at level {}",
				String.format("%02X", command), manager.getCurrentLevel(), e);
			sendError(out, e.getMessage());
		}
	}

	private void dispatch(byte command) {
		switch (command) {
			case InsistenceLayerProtocol.INCREASE -> manager.increaseLevel();
			case InsistenceLayerProtocol.DECREASE -> manager.decreaseLevel();
			case InsistenceLayerProtocol.RESET    -> manager.resetCurrentLevel();
			default -> throw new IllegalArgumentException("Unknown command: " + command);
		}
	}

	private void sendOk(DataOutputStream out) throws IOException {
		out.writeByte(InsistenceLayerProtocol.OK);
		out.flush();
	}

	private void sendError(DataOutputStream out, String message) throws IOException {
		out.writeByte(InsistenceLayerProtocol.ERROR);
		String safe = message != null ? message : "Unknown error";
		byte[] messageBytes = safe.getBytes(StandardCharsets.UTF_8);
		out.writeShort(messageBytes.length);
		out.write(messageBytes);
		out.flush();
	}

}
