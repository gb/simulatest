package org.simulatest.insistencelayer.connection;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.connection.ConnectionLayer;
import org.simulatest.insistencelayer.mock.ConnectionMock;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

public class ConnectionLayerTest {

	private ConnectionMock connection;
	private ConnectionLayer connectionLayer;
	private ConnectionMock spyConnectionMock;

	@Before
	public void setup() {
		connection = new ConnectionMock();
		spyConnectionMock = spy(connection);
		connectionLayer = new ConnectionLayer(spyConnectionMock);
	}

	@Test
	public void fakeCommitTest() throws SQLException {
		connectionLayer.commit();

		verify(spyConnectionMock, never()).commit();
	}

	@Test
	public void fakeRollbackTest() throws SQLException {
		connectionLayer.rollback();

		verify(spyConnectionMock, never()).rollback();
	}

	@Test
	public void autoCommitTest() throws SQLException {
		spyConnectionMock.setAutoCommit(true);

		assertTrue(spyConnectionMock.getAutoCommit());
		assertFalse(connectionLayer.getAutoCommit());
	}

}