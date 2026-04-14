package org.simulatest.insistencelayer;

import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC implementation of {@link InsistenceLayer} that uses savepoints
 * as its checkpoint mechanism. Each level corresponds to a named savepoint
 * on the underlying connection.
 */
public final class LocalInsistenceLayer implements InsistenceLayer {

	private static final String SAVEPOINT_PREFIX = "LAYER";
	private static final Logger logger = LoggerFactory.getLogger(LocalInsistenceLayer.class);

	private final ConnectionWrapper connection;
	private final Deque<Savepoint> savepoints;

	LocalInsistenceLayer(ConnectionWrapper connection) {
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
		wrapConnectionIfFirstLevel();
		createSavepoint();

		logger.info("Level increased to {}", getCurrentLevel());
	}

	private void wrapConnectionIfFirstLevel() {
		if (stackIsEmpty()) connection.wrap();
	}

	private void createSavepoint() {
		String savePointName = SAVEPOINT_PREFIX + (getCurrentLevel() + 1);

		try {
			savepoints.push(connection.setSavepoint(savePointName));
		} catch (SQLException exception) {
			String message = "Error creating the savepoint: " + savePointName;
			throw new InsistenceLayerException(message, exception);
		}
	}

	@Override
	public void resetCurrentLevel() {
		if (stackIsEmpty()) return;

		rollbackSavepoint(savepoints.peek());
		logger.info("Cleaned current level: {}", getCurrentLevel());
	}

	@Override
	public void decreaseLevel() {
		if (stackIsEmpty()) {
			throw new IllegalStateException("Cannot decrease level: already at level 0");
		}

		rollbackSavepoint(savepoints.pop());
		unwrapConnectionIfStackEmptied();

		logger.info("Level decreased to {}", getCurrentLevel());
	}

	private void unwrapConnectionIfStackEmptied() {
		if (stackIsEmpty()) connection.unwrap();
	}

	@Override
	public void setLevelTo(int level) {
		if (level < 0) throw new IllegalArgumentException("Level cannot be negative");
		logger.info("Setting level {} to {}", getCurrentLevel(), level);

		if (getCurrentLevel() > level) decreaseToLevel(level);
		else if (getCurrentLevel() < level) increaseToLevel(level);
	}

	private void increaseToLevel(int level) {
		while (getCurrentLevel() < level) increaseLevel();
	}

	// Optimization: intermediate levels are released (dropped) without rolling back,
	// since their data will be undone by the final decreaseLevel's rollback anyway.
	// Invariant on exit: current level == target.
	private void decreaseToLevel(int target) {
		while (getCurrentLevel() > target + 1) dropCurrentLevel();
		if (getCurrentLevel() == target + 1) decreaseLevel();
	}

	private void dropCurrentLevel() {
		try {
			connection.releaseSavepoint(savepoints.pop());
		} catch (SQLException exception) {
			throw new InsistenceLayerException(
					"Error dropping level " + (getCurrentLevel() + 1), exception);
		}
	}

	private void rollbackSavepoint(Savepoint savepoint) {
		try {
			connection.rollback(savepoint);
		} catch (SQLException exception) {
			throw new InsistenceLayerException(
					"Error rolling back savepoint at level " + getCurrentLevel(), exception);
		}
	}

	private boolean stackIsEmpty() {
		return getCurrentLevel() == 0;
	}

}
