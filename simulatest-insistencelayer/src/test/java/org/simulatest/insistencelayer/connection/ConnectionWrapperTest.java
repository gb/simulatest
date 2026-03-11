package org.simulatest.insistencelayer.connection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

public class ConnectionWrapperTest {

	private Connection realConnection;
	private ConnectionWrapper connectionWrapper;

	@Before
	public void setup() throws SQLException {
		realConnection = DriverManager.getConnection("jdbc:h2:~/.h2/test", "sa", "");
		connectionWrapper = new ConnectionWrapper(realConnection);
	}

	@Test
	public void shouldNotBePossibleCreateAnInstanceWithANullConnection() {
		try {
			new ConnectionWrapper((Connection) null);
			fail("was possible create an instance of ConnectionWrapper with a null Connection!");
		} catch (RuntimeException e) {
			assertEquals("Connection is null", e.getMessage());
		}
	}

	@Test
	public void whenConnectionWrapperIsCreatedTheConnectionIsReal() {
		assertFalse(connectionWrapper.isConnectionFake());
	}

	@Test
	public void whenInsistenceLayerIsStartedTheConnectionIsNotReal() {
		connectionWrapper.wrap();
		assertTrue(connectionWrapper.isConnectionFake());
	}

	@Test
	public void whenInsistenceLayerIsDisabledTheConnectionIsRealAgain() {
		connectionWrapper.wrap();
		connectionWrapper.unwrap();
		assertFalse(connectionWrapper.isConnectionFake());
	}

	@Test
	public void getConnectionShouldReturnAProxyConnection() {
		Connection proxy = connectionWrapper.getConnection();
		assertNotNull(proxy);
		assertTrue(java.lang.reflect.Proxy.isProxyClass(proxy.getClass()));
	}

	@Test
	public void proxyShouldDelegateStatementsToRealConnection() throws SQLException {
		assertNotNull(connectionWrapper.getConnection().createStatement());
	}

	@Test
	public void proxyShouldDelegateCreateStatementWithParameters() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		verify(mockConnection).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	@Test
	public void proxyShouldDelegatePrepareStatementWithParameters() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.getConnection().prepareStatement("SELECT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		verify(mockConnection).prepareStatement("SELECT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	@Test
	public void proxyShouldDelegateCreateStatementWithHoldability() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.getConnection().createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

		verify(mockConnection).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
	}

	@Test
	public void commitShouldNotDelegateWhenActive() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.wrap();

		wrapper.getConnection().commit();

		verify(mockConnection, never()).commit();
	}

	@Test
	public void rollbackShouldNotDelegateWhenActive() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.wrap();

		wrapper.getConnection().rollback();

		verify(mockConnection, never()).rollback();
	}

	@Test
	public void getAutoCommitShouldReturnFalseWhenActive() throws SQLException {
		connectionWrapper.wrap();
		assertFalse(connectionWrapper.getConnection().getAutoCommit());
	}

	@Test
	public void closeShouldAlwaysBeNoOp() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.getConnection().close();

		verify(mockConnection, never()).close();
	}

	@Test
	public void prepareCallShouldDelegateToRealConnection() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.getConnection().prepareCall("CALL my_proc()");

		verify(mockConnection).prepareCall("CALL my_proc()");
	}

	@Test
	public void nativeSQLShouldDelegateToRealConnection() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.getConnection().nativeSQL("SELECT 1");

		verify(mockConnection).nativeSQL("SELECT 1");
	}

}
