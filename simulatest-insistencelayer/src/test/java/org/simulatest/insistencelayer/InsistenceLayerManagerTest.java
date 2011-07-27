package org.simulatest.insistencelayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.atMost;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import java.sql.SQLException;
import java.sql.Savepoint;


import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.mock.ConnectionMock;

public class InsistenceLayerManagerTest {

	private ConnectionMock connection;
	private InsistenceLayerManager insistenceLayerManager;
	
	@Before 
	public void setup() throws SQLException {
		connection = new ConnectionMock();
        insistenceLayerManager = new InsistenceLayerManager(connection);
    }

	@Test
	public void shouldNotBePossibleCreateAnInstanceWithANullConnection() {
		try {
			insistenceLayerManager = new InsistenceLayerManager(null);
			fail("was possible create an instance of InsistenceLayer with a null Connection!");
		} catch (RuntimeException e) {
			assertEquals("Connection is null", e.getMessage());
		}
	}
	
	@Test
	public void initialValueShouldBeZero() {
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
	}
	
	@Test
	public void shouldIncreaseLevel() {	
		insistenceLayerManager.increaseLevel();
		assertEquals(1, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.increaseLevel();
		assertEquals(2, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.increaseLevel();
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
		
		assertEquals(3, connection.getSavepoints().size());
	}
	
	@Test
	public void shouldDecreaseLevel() {
		insistenceLayerManager.setLevelTo(3);
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.decreaseLevel();
		assertEquals(2, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.decreaseLevel();
		assertEquals(1, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.decreaseLevel();
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
		
		assertEquals(0, connection.getSavepoints().size());
	}
	
	@Test
	public void shouldDecreaseAllLevels() {
		insistenceLayerManager.setLevelTo(5);
		assertEquals(5, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.decreaseAllLevels();
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
		
		assertEquals(0, connection.getSavepoints().size());
	}
	
	@Test
	public void shouldNotSetLevelToNegativeValue() {
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
		
		try {
			insistenceLayerManager.setLevelTo(-1);
			fail("was possible set a negative level!");
		} catch (RuntimeException e) {
			assertEquals("Level cannot be negative", e.getMessage());
		}
	}
	
	@Test
	public void shouldSetLevel() {
		insistenceLayerManager.setLevelTo(3);
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(4);
		assertEquals(4, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(2);
		assertEquals(2, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(3);
		assertEquals(3, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(0);
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(1);
		assertEquals(1, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(0);
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
		
		assertEquals(0, connection.getSavepoints().size());
	}
	
	@Test
	public void shouldCallRollbackLevelJustOnceWhenUseSetLevel() throws SQLException {
		ConnectionMock spyConnectionMock = spy(connection);
		insistenceLayerManager = new InsistenceLayerManager(spyConnectionMock);
		
		insistenceLayerManager.setLevelTo(9);
		assertEquals(9, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.setLevelTo(1);
		assertEquals(1, insistenceLayerManager.getCurrentLevel());
		
		verify(spyConnectionMock, atMost(1)).rollback((Savepoint) any());
	}
	
	@Test
	public void shouldNotDecreaseLevelLesserThanZero() {
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
		
		insistenceLayerManager.decreaseLevel();
		
		assertEquals(0, insistenceLayerManager.getCurrentLevel());
	}
	
}