package org.simulatest.insistencelayer.sql;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;

public class ConnectionWrapperTest {

	private static final String URL = "jdbc:h2:mem:connectionwrappertest";

	private Connection realConnection;
	private ConnectionWrapper connectionWrapper;

	@Before
	public void setup() throws SQLException {
		realConnection = DriverManager.getConnection(URL, "sa", "");
		connectionWrapper = new ConnectionWrapper(realConnection);
	}

	@After
	public void closeConnection() throws SQLException {
		if (realConnection != null) realConnection.close();
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
		assertFalse(connectionWrapper.isActive());
	}

	@Test
	public void whenInsistenceLayerIsStartedTheConnectionIsNotReal() throws SQLException {
		connectionWrapper.pushLevel();
		assertTrue(connectionWrapper.isActive());
	}

	@Test
	public void whenInsistenceLayerIsDisabledTheConnectionIsRealAgain() throws SQLException {
		connectionWrapper.pushLevel();
		connectionWrapper.popLevel();
		assertFalse(connectionWrapper.isActive());
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
		wrapper.pushLevel();

		wrapper.getConnection().commit();

		verify(mockConnection, never()).commit();
	}

	@Test
	public void rollbackShouldNotDelegateWhenActive() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.pushLevel();

		wrapper.getConnection().rollback();

		verify(mockConnection, never()).rollback();
	}

	@Test
	public void getAutoCommitShouldReturnFalseWhenActive() throws SQLException {
		connectionWrapper.pushLevel();
		assertFalse(connectionWrapper.getConnection().getAutoCommit());
	}

	@Test
	public void outsideAnyLevelSetAutoCommitShouldReachTheRealConnection() throws SQLException {
		connectionWrapper.getConnection().setAutoCommit(true);

		assertTrue(realConnection.getAutoCommit());
	}

	@Test
	public void outsideAnyLevelSetAutoCommitTrueShouldPersistRowsForOtherConnections() throws SQLException {
		Connection proxy = connectionWrapper.getConnection();
		createSeedTable();

		proxy.setAutoCommit(true);
		proxy.createStatement().executeUpdate("INSERT INTO SEED VALUES ('kept')");

		assertEquals(1, seedRowsSeenByAnotherConnection());
	}

	@Test
	public void outsideAnyLevelRestoringAutoCommitAfterATransactionShouldPersistLaterRows() throws SQLException {
		Connection proxy = connectionWrapper.getConnection();
		createSeedTable();

		// What Spring's DataSourceTransactionManager does around one @Transactional block.
		proxy.setAutoCommit(false);
		proxy.createStatement().executeUpdate("INSERT INTO SEED VALUES ('in-tx')");
		proxy.commit();
		proxy.setAutoCommit(true);

		proxy.createStatement().executeUpdate("INSERT INTO SEED VALUES ('after-tx')");

		assertEquals(2, seedRowsSeenByAnotherConnection());
	}

	private void createSeedTable() throws SQLException {
		connectionWrapper.getConnection().createStatement().executeUpdate("CREATE TABLE SEED (NAME VARCHAR(50))");
	}

	// The in-memory database lives as long as realConnection, so a second connection sees only committed rows.
	private int seedRowsSeenByAnotherConnection() throws SQLException {
		try (Connection other = DriverManager.getConnection(URL, "sa", "")) {
			ResultSet rs = other.createStatement().executeQuery("SELECT COUNT(*) FROM SEED");
			rs.next();
			return rs.getInt(1);
		}
	}

	@Test
	public void closingTheLastLevelShouldRestoreTheAutoCommitTheConnectionHadBefore() throws SQLException {
		realConnection.setAutoCommit(false);

		connectionWrapper.pushLevel();
		connectionWrapper.popLevel();

		assertFalse(realConnection.getAutoCommit());
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

	@Test
	public void commitShouldPropagateReleaseSavepointFailureAndLeaveWrapperUsable() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		Savepoint first = mock(Savepoint.class);
		Savepoint second = mock(Savepoint.class);
		when(mockConnection.setSavepoint())
				.thenReturn(first)
				.thenReturn(second);
		SQLException underlying = new SQLException("release refused");
		doThrow(underlying).when(mockConnection).releaseSavepoint(first);

		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.pushLevel();
		wrapper.getConnection().commit();

		try {
			wrapper.getConnection().commit();
			fail("commit should propagate the releaseSavepoint SQLException");
		} catch (SQLException thrown) {
			assertEquals("underlying SQLException identity preserved",
					underlying.getMessage(), thrown.getMessage());
		}

		wrapper.getConnection().rollback();
		verify(mockConnection, never()).rollback();
	}

	@Test
	public void rollbackWithoutCommitShouldReturnToTheStartOfTheLevel() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		Savepoint levelStart = mock(Savepoint.class);
		when(mockConnection.setSavepoint("LAYER1")).thenReturn(levelStart);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.pushLevel();

		wrapper.getConnection().rollback();

		verify(mockConnection).rollback(levelStart);
	}

	@Test
	public void rollbackAfterResetShouldNotTargetTheCommitMadeBeforeTheReset() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		Savepoint levelStart = mock(Savepoint.class);
		Savepoint staleCommit = mock(Savepoint.class);
		when(mockConnection.setSavepoint("LAYER1")).thenReturn(levelStart);
		when(mockConnection.setSavepoint()).thenReturn(staleCommit);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.pushLevel();
		wrapper.getConnection().commit();

		wrapper.resetLevel();
		wrapper.getConnection().rollback();

		verify(mockConnection, never()).rollback(staleCommit);
		verify(mockConnection, times(2)).rollback(levelStart);
	}

	@Test
	public void popLevelShouldRestoreTheCommitOfTheLevelBelowAsRollbackTarget() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		Savepoint outerStart = mock(Savepoint.class);
		Savepoint outerCommit = mock(Savepoint.class);
		Savepoint innerStart = mock(Savepoint.class);
		when(mockConnection.setSavepoint("LAYER1")).thenReturn(outerStart);
		when(mockConnection.setSavepoint()).thenReturn(outerCommit);
		when(mockConnection.setSavepoint("LAYER2")).thenReturn(innerStart);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);
		wrapper.pushLevel();
		wrapper.getConnection().commit();
		wrapper.pushLevel();

		wrapper.getConnection().rollback();
		verify(mockConnection).rollback(innerStart);
		verify(mockConnection, never()).rollback(outerCommit);

		wrapper.popLevel();
		wrapper.getConnection().rollback();
		verify(mockConnection).rollback(outerCommit);
	}

}
