package org.simulatest.insistencelayer.mock;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.util.Stack;

import org.mockito.Mockito;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class ConnectionMock extends ConnectionWrapper implements Connection {
	
	private Stack<Savepoint> savepoints;
	private int savepointId = 0;
	private boolean autocommit;
	
	public ConnectionMock() {
		super(Mockito.mock(Connection.class));
		savepoints = new Stack<Savepoint>();
	}

	@Override
	public void setAutoCommit(boolean autoCommit) throws SQLException {
		this.autocommit = autoCommit;
	}

	@Override
	public boolean getAutoCommit() throws SQLException {
		return autocommit;
	}


	@Override
	public Savepoint setSavepoint() throws SQLException {
		return setSavepoint("Savepoint " + savepointId);
	}

	@Override
	public Savepoint setSavepoint(String name) throws SQLException {
		SavepointMock savepoint = new SavepointMock(savepointId, name);
		savepoints.add(savepoint);
		savepointId++;
		return savepoint;
	}
	
	public Stack<Savepoint> getSavepoints() {
		return savepoints;
	}

	@Override
	public void rollback(Savepoint savepoint) throws SQLException {
		savepoints.remove(savepoint);
	}

	@Override
	public void releaseSavepoint(Savepoint savepoint) throws SQLException {
		savepoints.remove(savepoint);
	}

}