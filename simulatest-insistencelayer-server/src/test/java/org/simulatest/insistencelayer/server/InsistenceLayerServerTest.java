package org.simulatest.insistencelayer.server;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.DriverManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.server.infra.InsistenceLayerServerException;

public class InsistenceLayerServerTest {

	private Connection connection;
	private InsistenceLayerServer server;

	@Before
	public void setUp() throws Exception {
		connection = DriverManager.getConnection("jdbc:h2:mem:servertest;DB_CLOSE_DELAY=-1");
		server = new InsistenceLayerServer(0, connection);
	}

	@After
	public void tearDown() throws Exception {
		server.stop();
		connection.close();
	}

	@Test
	public void shouldStartAndStop() {
		assertFalse(server.isRunning());
		server.start();
		assertTrue(server.isRunning());
		server.stop();
		assertFalse(server.isRunning());
	}

	@Test(expected = InsistenceLayerServerException.class)
	public void shouldThrowOnDoubleStart() {
		server.start();
		server.start();
	}

	@Test
	public void shouldAllowStopWhenNotStarted() {
		server.stop();
		assertFalse(server.isRunning());
	}
}
