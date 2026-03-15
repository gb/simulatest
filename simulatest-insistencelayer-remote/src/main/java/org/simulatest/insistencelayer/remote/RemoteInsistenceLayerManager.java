package org.simulatest.insistencelayer.remote;

import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Remote proxy for {@link InsistenceLayerManager}.
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
 * InsistenceLayerManager manager = new RemoteInsistenceLayerManager("localhost", 4242);
 * manager.increaseLevel();   // sent over TCP to the app process
 * manager.resetCurrentLevel();
 * manager.decreaseLevel();
 * }</pre>
 */
public class RemoteInsistenceLayerManager extends InsistenceLayerManager implements AutoCloseable {

	private static final Logger logger = LoggerFactory.getLogger(RemoteInsistenceLayerManager.class);

	private final InsistenceLayerClient client;
	private int level;

	/**
	 * Creates a remote manager that connects to the given server.
	 * The TCP connection is established lazily on the first command.
	 *
	 * @param host the server hostname
	 * @param port the server port
	 */
	public RemoteInsistenceLayerManager(String host, int port) {
		super();
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
		logger.info("[InsistenceLayer Remote] Level increased to {}", level);
	}

	/**
	 * Sends a decrease-level command to the remote server.
	 * On success, decrements the local level counter.
	 */
	@Override
	public void decreaseLevel() {
		client.sendCommand(InsistenceLayerProtocol.DECREASE);
		level--;
		logger.info("[InsistenceLayer Remote] Level decreased to {}", level);
	}

	/**
	 * Sends a reset-current-level command to the remote server.
	 * The level counter stays the same (reset rolls back data, not the savepoint stack).
	 */
	@Override
	public void resetCurrentLevel() {
		client.sendCommand(InsistenceLayerProtocol.RESET);
		logger.info("[InsistenceLayer Remote] Cleaned current level: {}", level);
	}

	/**
	 * Sets the remote savepoint level to the given target by sending the
	 * appropriate number of increase or decrease commands individually.
	 *
	 * <p>Overridden because the parent's {@code decreaseToLevel} calls private
	 * methods that access the null connection and savepoints fields.</p>
	 *
	 * @param level the target level (must be non-negative)
	 */
	@Override
	public void setLevelTo(int level) {
		if (level < 0) throw new IllegalArgumentException("Level cannot be negative");
		logger.info("[InsistenceLayer Remote] Setting level {} to {}", this.level, level);

		while (this.level > level) decreaseLevel();
		while (this.level < level) increaseLevel();
	}

	/**
	 * Decreases all savepoint levels back to zero by sending individual
	 * decrease commands for each level.
	 */
	@Override
	public void decreaseAllLevels() {
		setLevelTo(0);
	}

	/**
	 * Closes the underlying TCP connection to the server.
	 */
	@Override
	public void close() {
		client.close();
	}

}
