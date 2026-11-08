package org.simulatest.insistencelayer.infra.sql;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Objects;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;


/**
 * JDBC {@link Connection} wrapper that intercepts commit/rollback to stage
 * changes behind savepoints managed by the Insistence Layer.
 *
 * <p>The wrapper keeps a stack of levels. Each level owns the savepoint
 * created when it was pushed and remembers the last user {@code commit()}
 * made inside it. A user {@code rollback()} returns to that commit, or to
 * the start of the level when nothing was committed in it. It never goes
 * below the current level's savepoint, so the state a level started with
 * is always preserved.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe. JDBC connections are not
 * thread-safe by design, so this wrapper assumes exclusive use by one
 * thread at a time.</p>
 */
public final class ConnectionWrapper {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionWrapper.class);

	private static final String METHOD_COMMIT = "commit";
	private static final String METHOD_ROLLBACK = "rollback";
	private static final String METHOD_CLOSE = "close";
	private static final String METHOD_IS_CLOSED = "isClosed";
	private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
	private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
	private static final String LEVEL_SAVEPOINT_PREFIX = "LAYER";
	private static final String USER_COMMIT_SAVEPOINT = "USER_COMMIT";

	private final Connection realConnection;
	private final Connection proxy;
	private final Deque<Level> levels = new ArrayDeque<>();

	public ConnectionWrapper(DataSource source) {
		Objects.requireNonNull(source, "DataSource is null");

		try {
			this.realConnection = source.getConnection();
		} catch (SQLException e) {
			throw new InsistenceLayerException("Error obtaining connection from DataSource", e);
		}

		this.proxy = createProxy();
	}

	public ConnectionWrapper(Connection realConnection) {
		Objects.requireNonNull(realConnection, "Connection is null");
		this.realConnection = realConnection;
		this.proxy = createProxy();
	}

	public Connection getConnection() {
		return proxy;
	}

	/**
	 * Returns {@code true} while at least one level is open, meaning
	 * commit and rollback are being intercepted.
	 */
	public boolean isActive() {
		return !levels.isEmpty();
	}

	/**
	 * Returns how many levels are open.
	 */
	public int levelCount() {
		return levels.size();
	}

	/**
	 * Opens a level: creates the savepoint {@code LAYER<n>} for the new
	 * depth and makes it the floor for user rollbacks. Opening the first
	 * level also disables autocommit on the real connection.
	 */
	public void pushLevel() throws SQLException {
		if (levels.isEmpty()) wrap();
		String savepointName = LEVEL_SAVEPOINT_PREFIX + (levels.size() + 1);
		levels.push(new Level(realConnection.setSavepoint(savepointName)));
	}

	/**
	 * Rolls back to the start of the current level and forgets the user
	 * commits made inside it.
	 */
	public void resetLevel() throws SQLException {
		Level current = currentLevel();
		realConnection.rollback(current.savepoint);
		current.lastCommit = null;
	}

	/**
	 * Rolls back to the start of the current level and closes it. Closing
	 * the last level also restores autocommit on the real connection.
	 */
	public void popLevel() throws SQLException {
		realConnection.rollback(currentLevel().savepoint);
		closeCurrentLevel();
	}

	/**
	 * Closes the current level without rolling back, releasing its
	 * savepoint. Meant for when a lower level is about to be rolled back
	 * anyway, which undoes this level's data as well.
	 */
	public void dropLevel() throws SQLException {
		realConnection.releaseSavepoint(currentLevel().savepoint);
		closeCurrentLevel();
	}

	private Level currentLevel() {
		Level current = levels.peek();
		if (current == null) throw new IllegalStateException("No level is open");
		return current;
	}

	private void closeCurrentLevel() {
		levels.pop();
		if (levels.isEmpty()) unwrap();
	}

	private void wrap() {
		try {
			realConnection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new InsistenceLayerException("Error enabling insistence layer", e);
		}
	}

	private void unwrap() {
		try {
			realConnection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new InsistenceLayerException("Error disabling insistence layer", e);
		}
	}

	private Connection createProxy() {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			new ConnectionInvocationHandler(this)
		);
	}

	private static final class ConnectionInvocationHandler implements InvocationHandler {

		private final ConnectionWrapper wrapper;

		ConnectionInvocationHandler(ConnectionWrapper wrapper) {
			this.wrapper = wrapper;
		}

		@Override
		public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();

			// Always intercepted: prevent frameworks (Spring, connection pools)
			// from closing the real connection or re-enabling autocommit.
			if (METHOD_CLOSE.equals(methodName)) return null;
			if (METHOD_SET_AUTO_COMMIT.equals(methodName)) {
				if (!wrapper.isActive()) wrapper.realConnection.setAutoCommit(false);
				return null;
			}

			if (wrapper.isActive()) {
				if (METHOD_COMMIT.equals(methodName)) { wrapper.handleCommit(); return null; }
				if (METHOD_ROLLBACK.equals(methodName) && (args == null || args.length == 0)) {
					wrapper.handleRollback();
					return null;
				}
				if (METHOD_GET_AUTO_COMMIT.equals(methodName)) return false;
				// Report the connection as open so pools/frameworks don't reclaim it.
				if (METHOD_IS_CLOSED.equals(methodName)) return false;
			}

			try {
				return method.invoke(wrapper.realConnection, args);
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
	}

	private void handleCommit() throws SQLException {
		logger.debug("Commit (bumping savepoint)");
		Level current = currentLevel();
		if (current.lastCommit != null) {
			realConnection.releaseSavepoint(current.lastCommit);
		}
		current.lastCommit = realConnection.setSavepoint(USER_COMMIT_SAVEPOINT);
	}

	private void handleRollback() throws SQLException {
		logger.debug("Rollback");
		realConnection.rollback(currentLevel().rollbackTarget());
	}

	/**
	 * One open level: the savepoint it started with and the last user
	 * commit made inside it, if any.
	 */
	private static final class Level {

		private final Savepoint savepoint;
		private Savepoint lastCommit;

		Level(Savepoint savepoint) {
			this.savepoint = savepoint;
		}

		Savepoint rollbackTarget() {
			return lastCommit != null ? lastCommit : savepoint;
		}

	}

}
