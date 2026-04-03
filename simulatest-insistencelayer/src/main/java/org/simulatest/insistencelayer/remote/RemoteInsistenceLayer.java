package org.simulatest.insistencelayer.remote;

import org.simulatest.insistencelayer.InsistenceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote proxy for {@link InsistenceLayer}.
 *
 * <p>Drop-in replacement that forwards {@code increaseLevel()}, {@code decreaseLevel()},
 * and {@code resetCurrentLevel()} over TCP to an {@link InsistenceLayerServer} running
 * in the application process. The local level counter mirrors remote state so that
 * {@code getCurrentLevel()} returns instantly without a round-trip.</p>
 *
 * <p>Use this in the test process when the application under test runs in a separate
 * JVM (e.g. Spring Boot in Docker, a Selenium target, or any remote service).</p>
 *
 * <h3>Usage:</h3>
 * <pre>{@code
 * InsistenceLayer manager = new RemoteInsistenceLayer("localhost", 4242);
 * manager.increaseLevel();   // sent over TCP to the app process
 * manager.resetCurrentLevel();
 * manager.decreaseLevel();
 * }</pre>
 */
public final class RemoteInsistenceLayer implements InsistenceLayer, AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(RemoteInsistenceLayer.class);

	private final InsistenceLayerClient client;
	private int level;

	/**
	 * Creates a remote manager that connects to the given server.
	 * The TCP connection is established lazily on the first command.
	 *
	 * @param host the server hostname
	 * @param port the server port
	 */
	public RemoteInsistenceLayer(String host, int port) {
		this.client = new InsistenceLayerClient(host, port);
		this.level = 0;
	}

	/**
	 * Returns the current savepoint level, tracked locally to avoid a round-trip.
	 * The counter is updated only after a successful remote command, so it stays
	 * consistent with the server even when commands fail.
	 */
	@Override
	public int getCurrentLevel() {
		return level;
	}

	/**
	 * Sends an increase-level command to the remote server.
	 * On success, increments the local level counter.
	 */
	@Override
	public void increaseLevel() {
		client.sendCommand(InsistenceLayerProtocol.INCREASE);
		level++;
		logger.info("Level increased to {}", level);
	}

	/**
	 * Sends a decrease-level command to the remote server.
	 * On success, decrements the local level counter.
	 */
	@Override
	public void decreaseLevel() {
		client.sendCommand(InsistenceLayerProtocol.DECREASE);
		level--;
		logger.info("Level decreased to {}", level);
	}

	/**
	 * Sends a reset-current-level command to the remote server.
	 * The level counter stays the same (reset rolls back data, not the savepoint stack).
	 */
	@Override
	public void resetCurrentLevel() {
		client.sendCommand(InsistenceLayerProtocol.RESET);
		logger.info("Cleaned current level: {}", level);
	}

	/**
	 * Closes the underlying TCP connection to the server.
	 */
	@Override
	public void close() {
		client.close();
	}

}
