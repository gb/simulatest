package org.simulatest.insistencelayer.mock;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.Connection;
import java.sql.Savepoint;
import java.util.Stack;

public class ConnectionMock {

	private final Connection connection;
	private final Stack<Savepoint> savepoints = new Stack<Savepoint>();
	private int savepointId = 0;
	private boolean autocommit = true;

	public ConnectionMock() {
		this.connection = createProxy();
	}

	public Connection getConnection() {
		return connection;
	}

	public Stack<Savepoint> getSavepoints() {
		return savepoints;
	}

	private Connection createProxy() {
		return (Connection) Proxy.newProxyInstance(
			Connection.class.getClassLoader(),
			new Class<?>[] { Connection.class },
			new MockHandler()
		);
	}

	private class MockHandler implements InvocationHandler {

		private static final String METHOD_SET_AUTO_COMMIT = "setAutoCommit";
		private static final String METHOD_GET_AUTO_COMMIT = "getAutoCommit";
		private static final String METHOD_SET_SAVEPOINT = "setSavepoint";
		private static final String METHOD_ROLLBACK = "rollback";
		private static final String METHOD_RELEASE_SAVEPOINT = "releaseSavepoint";

		@Override
		public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
			switch (method.getName()) {
				case METHOD_SET_AUTO_COMMIT:
					autocommit = (Boolean) args[0];
					return null;
				case METHOD_GET_AUTO_COMMIT:
					return autocommit;
				case METHOD_SET_SAVEPOINT:
					String name = (args != null && args.length > 0) ? (String) args[0] : "Savepoint " + savepointId;
					SavepointMock savepoint = new SavepointMock(savepointId, name);
					savepoints.add(savepoint);
					savepointId++;
					return savepoint;
				case METHOD_ROLLBACK:
					if (args != null && args.length > 0) savepoints.remove((Savepoint) args[0]);
					return null;
				case METHOD_RELEASE_SAVEPOINT:
					savepoints.remove((Savepoint) args[0]);
					return null;
				default:
					return defaultValue(method.getReturnType());
			}
		}

		private Object defaultValue(Class<?> type) {
			if (type == boolean.class) return false;
			if (type == int.class) return 0;
			if (type == long.class) return 0L;
			return null;
		}
	}

}
