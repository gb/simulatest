package org.simulatest.insistencelayer.connection;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Objects;

import javax.sql.DataSource;

import org.simulatest.insistencelayer.infra.InsistenceLayerException;


public class ConnectionWrapper {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionWrapper.class);

	private static final String METHOD_COMMIT = "commit";
	private static final String METHOD_ROLLBACK = "rollback";
	private static final String METHOD_CLOSE = "close";
	private static final String METHOD_IS_CLOSED = "isClosed";
	private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
	private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
	private static final String USER_COMMIT_SAVEPOINT = "USER_COMMIT";

	private final Connection realConnection;
	private final Connection proxy;
	private boolean active;
	private Savepoint lastCommitSavepoint;

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

	public void wrap() {
		try {
			realConnection.setAutoCommit(false);
		} catch (SQLException e) {
			throw new InsistenceLayerException("Error enabling insistence layer", e);
		}
		active = true;
	}

	public void unwrap() {
		active = false;
		lastCommitSavepoint = null;
		try {
			realConnection.setAutoCommit(true);
		} catch (SQLException e) {
			throw new InsistenceLayerException("Error disabling insistence layer", e);
		}
	}

	public boolean isConnectionFake() {
		return active;
	}

	public Savepoint setSavepoint(String name) throws SQLException {
		return realConnection.setSavepoint(name);
	}

	public void rollback(Savepoint savepoint) throws SQLException {
		realConnection.rollback(savepoint);
	}

	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		realConnection.releaseSavepoint(savepoint);
	}

	private Connection createProxy() {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			new ConnectionInvocationHandler()
		);
	}

	private class ConnectionInvocationHandler implements InvocationHandler {

		@Override
		public Object invoke(Object proxyObj, Method method, Object[] args) throws Throwable {
			String methodName = method.getName();

			// Always intercepted regardless of active state
			if (METHOD_CLOSE.equals(methodName)) {
				return null;
			}
			if (METHOD_SET_AUTO_COMMIT.equals(methodName)) {
				if (!active) realConnection.setAutoCommit(false);
				return null;
			}

			// Intercepted only when active
			if (active) {
				if (METHOD_COMMIT.equals(methodName)) {
					handleCommit();
					return null;
				}
				if (METHOD_ROLLBACK.equals(methodName) && (args == null || args.length == 0)) {
					handleRollback();
					return null;
				}
				if (METHOD_GET_AUTO_COMMIT.equals(methodName)) {
					return false;
				}
				if (METHOD_IS_CLOSED.equals(methodName)) {
					return false;
				}
			}

			try {
				return method.invoke(realConnection, args);
			} catch (InvocationTargetException e) {
				throw e.getCause();
			}
		}
	}

	private void handleCommit() throws SQLException {
		logger.debug("Commit (bumping savepoint)");
		if (lastCommitSavepoint != null) {
			realConnection.releaseSavepoint(lastCommitSavepoint);
		}
		lastCommitSavepoint = realConnection.setSavepoint(USER_COMMIT_SAVEPOINT);
	}

	private void handleRollback() throws SQLException {
		logger.debug("Rollback");
		if (lastCommitSavepoint != null) {
			realConnection.rollback(lastCommitSavepoint);
		}
	}

}
