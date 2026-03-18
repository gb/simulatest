package org.simulatest.insistencelayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.sql.Savepoint;

import org.junit.Before;
import org.junit.Test;
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
	public void shouldSetLevel() {
		insistenceLayer.setLevelTo(3);
		assertEquals(3, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(4);
		assertEquals(4, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(2);
		assertEquals(2, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(3);
		assertEquals(3, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(0);
		assertEquals(0, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(1);
		assertEquals(1, insistenceLayer.getCurrentLevel());

		insistenceLayer.setLevelTo(0);
		assertEquals(0, insistenceLayer.getCurrentLevel());

		assertEquals(0, connectionMock.getSavepoints().size());
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

}
