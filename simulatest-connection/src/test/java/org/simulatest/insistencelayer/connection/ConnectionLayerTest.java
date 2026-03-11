package org.simulatest.insistencelayer.connection;

import java.sql.CallableStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

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

	@Test
	public void prepareCallShouldDelegateToWrappedConnection() throws SQLException {
		connectionLayer.prepareCall("CALL my_proc()");

		verify(spyConnectionMock).prepareCall("CALL my_proc()");
	}

	@Test
	public void nativeSQLShouldDelegateToWrappedConnection() throws SQLException {
		connectionLayer.nativeSQL("SELECT 1");

		verify(spyConnectionMock).nativeSQL("SELECT 1");
	}

	@Test
	public void createStatementShouldPassResultSetParameters() throws SQLException {
		connectionLayer.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		verify(spyConnectionMock).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	@Test
	public void prepareStatementShouldPassResultSetParameters() throws SQLException {
		connectionLayer.prepareStatement("SELECT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		verify(spyConnectionMock).prepareStatement("SELECT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	@Test
	public void createStatementWithHoldabilityShouldPassAllParameters() throws SQLException {
		connectionLayer.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

		verify(spyConnectionMock).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
	}

}