package org.simulatest.insistencelayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.sql.Savepoint;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;
import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;

public class InsistenceLayerTest {

	private ConnectionMock connectionMock;
	private ConnectionWrapper connection;
	private LocalInsistenceLayer insistenceLayer;

	@Before
	public void setup() throws SQLException {
		connectionMock = new ConnectionMock();
		connection = new ConnectionWrapper(connectionMock.getConnection());
		insistenceLayer = new LocalInsistenceLayer(connection);
	}

	@Test
	public void shouldNotBePossibleCreateAnInstanceWithANullConnection() {
		try {
			insistenceLayer = new LocalInsistenceLayer(null);
			fail("was possible create an instance of InsistenceLayer with a null Connection!");
		} catch (RuntimeException e) {
			assertEquals("Connection is null", e.getMessage());
		}
	}

	@Test
	public void initialValueShouldBeZero() {
		assertEquals(0, insistenceLayer.getCurrentLevel());
	}

	@Test
	public void shouldIncreaseLevel() {
		insistenceLayer.increaseLevel();
		assertEquals(1, insistenceLayer.getCurrentLevel());

		insistenceLayer.increaseLevel();
		assertEquals(2, insistenceLayer.getCurrentLevel());

		insistenceLayer.increaseLevel();
		assertEquals(3, insistenceLayer.getCurrentLevel());

		assertEquals(3, connectionMock.getSavepoints().size());
	}

	@Test
	public void shouldDecreaseLevel() {
		insistenceLayer.setLevelTo(3);
		assertEquals(3, insistenceLayer.getCurrentLevel());

		insistenceLayer.decreaseLevel();
		assertEquals(2, insistenceLayer.getCurrentLevel());

		insistenceLayer.decreaseLevel();
		assertEquals(1, insistenceLayer.getCurrentLevel());

		insistenceLayer.decreaseLevel();
		assertEquals(0, insistenceLayer.getCurrentLevel());

		assertEquals(0, connectionMock.getSavepoints().size());
	}

	@Test
	public void shouldDecreaseAllLevels() {
		insistenceLayer.setLevelTo(5);
		assertEquals(5, insistenceLayer.getCurrentLevel());

		insistenceLayer.decreaseAllLevels();
		assertEquals(0, insistenceLayer.getCurrentLevel());

		assertEquals(0, connectionMock.getSavepoints().size());
	}

	@Test
	public void shouldNotSetLevelToNegativeValue() {
		assertEquals(0, insistenceLayer.getCurrentLevel());

		try {
			insistenceLayer.setLevelTo(-1);
			fail("was possible set a negative level!");
		} catch (RuntimeException e) {
			assertEquals("Level cannot be negative", e.getMessage());
		}
	}

	@Test
	public void setLevelToShouldAdjustStackUpAndDownAndBackToZero() {
		insistenceLayer.setLevelTo(3);
		assertEquals("raise from zero", 3, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(4);
		assertEquals("increase from existing level", 4, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(2);
		assertEquals("decrease to lower level", 2, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(0);
		assertEquals("pop back to zero", 0, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(1);
		assertEquals("re-increase after zero", 1, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(0);
		assertEquals("no stray savepoints remain", 0, connectionMock.getSavepoints().size());
	}

	@Test
	public void shouldCallRollbackLevelJustOnceWhenUseSetLevel() throws SQLException {
		ConnectionWrapper spyConnection = spy(connection);
		insistenceLayer = new LocalInsistenceLayer(spyConnection);

		insistenceLayer.setLevelTo(9);
		assertEquals(9, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(1);
		assertEquals(1, insistenceLayer.getCurrentLevel());

		verify(spyConnection, atMost(1)).rollback((Savepoint) any());
	}

	@Test(expected = IllegalStateException.class)
	public void shouldThrowWhenDecreasingLevelAtZero() {
		assertEquals(0, insistenceLayer.getCurrentLevel());

		insistenceLayer.decreaseLevel();
	}

	@Test
	public void increaseLevelShouldLeaveStackEmptyWhenSetSavepointFails() throws SQLException {
		ConnectionWrapper failingConnection = mock(ConnectionWrapper.class);
		SQLException underlying = new SQLException("savepoint limit reached");
		when(failingConnection.setSavepoint(anyString())).thenThrow(underlying);
		LocalInsistenceLayer layerUnderTest = new LocalInsistenceLayer(failingConnection);

		assertEquals("guard: starts at level 0", 0, layerUnderTest.getCurrentLevel());
		try {
			layerUnderTest.increaseLevel();
			fail("increaseLevel should have wrapped and rethrown the SQLException");
		} catch (InsistenceLayerException thrown) {
			assertSame("underlying SQLException preserved as cause", underlying, thrown.getCause());
		}

		assertEquals("level must not advance when savepoint creation fails",
				0, layerUnderTest.getCurrentLevel());
	}

	@Test
	public void setLevelToShouldWrapReleaseSavepointFailureAsInsistenceException() throws SQLException {
		ConnectionWrapper spyConnection = spy(connection);
		SQLException underlying = new SQLException("release refused");
		doThrow(underlying).when(spyConnection).releaseSavepoint((Savepoint) any());
		LocalInsistenceLayer layerUnderTest = new LocalInsistenceLayer(spyConnection);
		layerUnderTest.setLevelTo(4);

		try {
			layerUnderTest.setLevelTo(1);
			fail("setLevelTo should have rethrown the releaseSavepoint failure");
		} catch (InsistenceLayerException thrown) {
			assertSame("underlying SQLException preserved as cause",
					underlying, thrown.getCause());
			assertTrue("error message should name the level being dropped, was: " + thrown.getMessage(),
					thrown.getMessage().contains("dropping level"));
		}

		assertTrue("level must still reflect reality of the savepoint stack; reset for teardown",
				layerUnderTest.getCurrentLevel() >= 1);
	}

}
