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
import java.sql.Statement;


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

	@Test
	public void createStatementShouldPassResultSetParameters() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		verify(mockConnection).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	@Test
	public void prepareStatementShouldPassResultSetParameters() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.prepareStatement("SELECT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);

		verify(mockConnection).prepareStatement("SELECT 1", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
	}

	@Test
	public void createStatementWithHoldabilityShouldPassAllParameters() throws SQLException {
		Connection mockConnection = mock(Connection.class);
		ConnectionWrapper wrapper = new ConnectionWrapper(mockConnection);

		wrapper.createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);

		verify(mockConnection).createStatement(ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY, ResultSet.HOLD_CURSORS_OVER_COMMIT);
	}

}