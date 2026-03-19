package org.simulatest.insistencelayer.remote;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TCP client for the remote Insistence Layer protocol.
 *
 * <p>Sends single-byte commands and reads single-byte responses. On error responses,
 * reads a length-prefixed error message and throws {@link InsistenceLayerException}.</p>
 *
 * <p>This is the "Humble Object", it knows how to talk over a socket but
 * contains no savepoint logic. Thread-safe for sequential use (one command at a time).</p>
 */
public class InsistenceLayerClient implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(InsistenceLayerClient.class);

	private final String host;
	private final int port;
	private Socket socket;
	private DataOutputStream out;
	private DataInputStream in;

	/**
	 * Creates a client targeting the given host and port. Does not connect immediately;
	 * the connection is established on the first command via {@link #connect()}.
	 *
	 * @param host the server hostname
	 * @param port the server port
	 */
	public InsistenceLayerClient(String host, int port) {
		this.host = host;
		this.port = port;
	}

	/**
	 * Opens the TCP connection to the server. Called automatically on first command
	 * if not already connected.
	 *
	 * @throws InsistenceLayerException if the connection cannot be established
	 */
	public void connect() {
		try {
			logger.debug("Connecting to {}:{}", host, port);
			socket = new Socket(host, port);
			out = new DataOutputStream(socket.getOutputStream());
			in = new DataInputStream(socket.getInputStream());
			logger.debug("Connected to {}:{}", host, port);
		} catch (IOException e) {
			logger.error("Failed to connect to {}:{}", host, port, e);
			throw new InsistenceLayerException(
				"Cannot connect to Insistence Layer server at " + host + ":" + port, e);
		}
	}

	/**
	 * Sends a command byte and waits for the response. On success ({@link InsistenceLayerProtocol#OK}),
	 * returns normally. On error ({@link InsistenceLayerProtocol#ERROR}), reads the error message
	 * and throws {@link InsistenceLayerException}.
	 *
	 * @param command one of {@link InsistenceLayerProtocol#INCREASE},
	 *                {@link InsistenceLayerProtocol#DECREASE}, or
	 *                {@link InsistenceLayerProtocol#RESET}
	 * @throws InsistenceLayerException if the command fails or the connection is broken
	 */
	public void sendCommand(byte command) {
		ensureConnected();

		try {
			out.writeByte(command);
			out.flush();
			readResponse();
		} catch (InsistenceLayerException e) {
			String context = String.format("Command 0x%02X failed on %s:%d", command, host, port);
			logger.error("{}: {}", context, e.getMessage());
			throw new InsistenceLayerException(context + ": " + e.getMessage(), e);
		} catch (IOException e) {
			resetConnection();
			logger.error("Lost connection to {}:{} during command 0x{}",
				host, port, String.format("%02X", command), e);
			throw new InsistenceLayerException(
				"Lost connection to Insistence Layer server at " + host + ":" + port, e);
		}
	}

	private void ensureConnected() {
		if (socket == null || socket.isClosed()) {
			connect();
		}
	}

	/**
	 * Closes the socket and nulls all I/O state so that {@link #ensureConnected()}
	 * will open a fresh connection on the next command.
	 */
	private void resetConnection() {
		try {
			if (socket != null && !socket.isClosed()) {
				socket.close();
			}
		} catch (IOException e) {
			logger.debug("Error closing dead connection to {}:{}", host, port, e);
		} finally {
			socket = null;
			out = null;
			in = null;
		}
	}

	private void readResponse() throws IOException {
		byte response = in.readByte();
		if (response == InsistenceLayerProtocol.ERROR) {
			throw new InsistenceLayerException("Remote Insistence Layer error: " + readErrorMessage());
		}
	}

	private String readErrorMessage() throws IOException {
		int length = in.readUnsignedShort();
		byte[] bytes = new byte[length];
		in.readFully(bytes);
		return new String(bytes, StandardCharsets.UTF_8);
	}

	/**
	 * Closes the TCP connection.
	 */
	@Override
	public void close() {
		resetConnection();
	}

}
