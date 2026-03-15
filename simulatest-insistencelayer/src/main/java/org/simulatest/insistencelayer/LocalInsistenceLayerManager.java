package org.simulatest.insistencelayer;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.InsistenceLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC implementation of {@link InsistenceLayerManager} that uses savepoints
 * as its checkpoint mechanism. Each level corresponds to a named savepoint
 * on the underlying connection.
 */
public class LocalInsistenceLayerManager implements InsistenceLayerManager {

	private static final String PREFIX_SAVEPOINT = "LAYER";
	private static final Logger logger = LoggerFactory.getLogger(LocalInsistenceLayerManager.class);

	private final ConnectionWrapper connection;
	private final Deque<Savepoint> savepoints;

	LocalInsistenceLayerManager(ConnectionWrapper connection) {
		Objects.requireNonNull(connection, "Connection is null");
		this.connection = connection;
		this.savepoints = new ArrayDeque<>();
	}

	@Override
	public int getCurrentLevel() {
		return savepoints.size();
	}

	@Override
	public void increaseLevel() {
		setup();
		createSavepoint();

		logger.info("[InsistenceLayer] Level increased to {}", getCurrentLevel());
	}

	private void setup() {
		if (isDisabled()) connection.wrap();
	}

	private void createSavepoint() {
		String savePointName = PREFIX_SAVEPOINT + (getCurrentLevel() + 1);

		try {
			savepoints.push(connection.setSavepoint(savePointName));
		} catch (SQLException exception) {
			String message = "Error creating the savepoint: " + savePointName;
			throw new InsistenceLayerException(message, exception);
		}
	}

	@Override
	public void resetCurrentLevel() {
		if (isDisabled()) return;

		rollbackSavepoint(savepoints.peek());
		logger.info("[InsistenceLayer] Cleaned current level: {}", getCurrentLevel());
	}

	@Override
	public void decreaseLevel() {
		if (isDisabled()) {
			throw new IllegalStateException("Cannot decrease level: already at level 0");
		}

		rollbackSavepoint(savepoints.pop());
		tearDown();

		logger.info("[InsistenceLayer] Level decreased to {}", getCurrentLevel());
	}

	private void tearDown() {
		if (isDisabled()) connection.unwrap();
	}

	@Override
	public void decreaseAllLevels() {
		setLevelTo(0);
	}

	@Override
	public void setLevelTo(int level) {
		if (level < 0) throw new IllegalArgumentException("Level cannot be negative");
		logger.info("[InsistenceLayer] Setting level {} to {}", getCurrentLevel(), level);

		if (getCurrentLevel() > level) decreaseToLevel(level);
		else if (getCurrentLevel() < level ) increaseToLevel(level);
	}

	private void increaseToLevel(int level) {
		while (getCurrentLevel() < level) increaseLevel();
	}

	private void decreaseToLevel(int level) {
		while (getCurrentLevel() - 1 > level) dropCurrentLevel();
		if (getCurrentLevel() == level + 1) decreaseLevel();
	}

	private void dropCurrentLevel() {
		try {
			connection.releaseSavepoint(savepoints.pop());
		} catch (SQLException exception) {
			throw new InsistenceLayerException("Error dropping the current level", exception);
		}
	}

	private void rollbackSavepoint(Savepoint savepoint) {
		try {
			connection.rollback(savepoint);
		} catch (SQLException exception) {
			throw new InsistenceLayerException("Error rollbacking the savepoint", exception);
		}
	}

	private boolean isDisabled() {
		return getCurrentLevel() == 0;
	}

}
