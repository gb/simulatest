package org.simulatest.insistencelayer;

import java.sql.SQLException;
import java.util.Objects;

import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JDBC implementation of {@link InsistenceLayer} that uses savepoints
 * as its checkpoint mechanism. Each level corresponds to a named savepoint
 * on the underlying connection, kept by the {@link ConnectionWrapper}.
 */
public final class LocalInsistenceLayer implements InsistenceLayer {

	private static final Logger logger = LoggerFactory.getLogger(LocalInsistenceLayer.class);

	private final ConnectionWrapper connection;

	LocalInsistenceLayer(ConnectionWrapper connection) {
		Objects.requireNonNull(connection, "Connection is null");
		this.connection = connection;
	}

	@Override
	public int getCurrentLevel() {
		return connection.levelCount();
	}

	@Override
	public void increaseLevel() {
		try {
			connection.pushLevel();
		} catch (SQLException exception) {
			throw new InsistenceLayerException(
					"Error creating savepoint for level " + (getCurrentLevel() + 1), exception);
		}

		logger.info("Level increased to {}", getCurrentLevel());
	}

	@Override
	public void resetCurrentLevel() {
		if (stackIsEmpty()) return;

		try {
			connection.resetLevel();
		} catch (SQLException exception) {
			throw rollbackFailure(exception);
		}

		logger.info("Cleaned current level: {}", getCurrentLevel());
	}

	@Override
	public void decreaseLevel() {
		requireOpenLevel();

		try {
			connection.popLevel();
		} catch (SQLException exception) {
			throw rollbackFailure(exception);
		}

		logger.info("Level decreased to {}", getCurrentLevel());
	}

	// Overrides the interface default to roll back once instead of twice: going
	// back to the start of the level below also undoes the current level.
	@Override
	public void decreaseLevelAndReset() {
		requireOpenLevel();

		try {
			connection.popLevelAndReset();
		} catch (SQLException exception) {
			throw rollbackFailure(exception);
		}

		logger.info("Level decreased to {} and cleaned", getCurrentLevel());
	}

	// Overrides the interface default to optimize the decrease path:
	// intermediate savepoints are released (dropped) instead of individually
	// rolled back, since their data is undone by the final decreaseLevel.
	@Override
	public void setLevelTo(int level) {
		if (level < 0) throw new IllegalArgumentException("Level cannot be negative");
		logger.info("Setting level {} to {}", getCurrentLevel(), level);

		while (getCurrentLevel() > level + 1) dropCurrentLevel();
		if (getCurrentLevel() == level + 1) decreaseLevel();
		while (getCurrentLevel() < level) increaseLevel();
	}

	private void dropCurrentLevel() {
		try {
			connection.dropLevel();
		} catch (SQLException exception) {
			throw new InsistenceLayerException(
					"Error releasing savepoint at level " + getCurrentLevel(), exception);
		}
	}

	private InsistenceLayerException rollbackFailure(SQLException exception) {
		return new InsistenceLayerException(
				"Error rolling back savepoint at level " + getCurrentLevel(), exception);
	}

	private void requireOpenLevel() {
		if (stackIsEmpty()) throw new IllegalStateException("Cannot decrease level: already at level 0");
	}

	private boolean stackIsEmpty() {
		return getCurrentLevel() == 0;
	}

	@Override
	public String toString() {
		return "LocalInsistenceLayer[level=" + getCurrentLevel() + "]";
	}

}
