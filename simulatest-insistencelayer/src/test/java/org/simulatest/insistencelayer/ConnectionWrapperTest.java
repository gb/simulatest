package org.simulatest.insistencelayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;

public class ConnectionWrapperTest {
	
	private Connection connection;
	private ConnectionWrapper connectionWrapper;
	
	@Before 
	public void setup() throws SQLException {
		connection = DriverManager.getConnection("jdbc:h2:~/.h2/test", "sa", "");
		connectionWrapper = new ConnectionWrapper(connection);
    }
	
	@Test
	public void shouldNotBePossibleCreateAnInstanceWithANullConnection() throws SQLException {
		try {
			connectionWrapper = new ConnectionWrapper(null);
			fail("was possible create an instance of ConnectionWrapper with a null Connection!");
		} catch (RuntimeException e) {
			assertEquals("Connection is null", e.getMessage());
		}
	}
	
	@Test
	public void whenConnectionWrapperIsCreatedTheConnectionIsReal() throws SQLException {
		assertFalse(connectionWrapper.isConnectionFake());
	}
	
	@Test
	public void whenInsistenceLayerIsStartedTheConnectionIsNotReal() throws SQLException {
		connectionWrapper.wrap();
		
		assertTrue(connectionWrapper.isConnectionFake());
	}
	
	@Test
	public void whenInsistenceLayerIsDisabledTheConnectionIsRealAgain() throws SQLException {
		connectionWrapper.wrap();
		connectionWrapper.unwrap();
		
		assertFalse(connectionWrapper.isConnectionFake());
	}

}