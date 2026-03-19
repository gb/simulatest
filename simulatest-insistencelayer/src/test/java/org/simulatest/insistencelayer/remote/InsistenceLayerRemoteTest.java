package org.simulatest.insistencelayer.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.infra.exception.InsistenceLayerException;
import org.simulatest.insistencelayer.remote.InsistenceLayerServer;
import org.simulatest.insistencelayer.remote.RemoteInsistenceLayer;

public class InsistenceLayerRemoteTest {

	private Connection jdbcConnection;
	private InsistenceLayer serverManager;
	private InsistenceLayerServer server;
	private RemoteInsistenceLayer remoteManager;

	@Before
	public void setup() throws SQLException, IOException {
		jdbcConnection = DriverManager.getConnection("jdbc:h2:mem:remote_test;DB_CLOSE_DELAY=-1");
		ConnectionWrapper wrapper = new ConnectionWrapper(jdbcConnection);
		serverManager = InsistenceLayerFactory.build(wrapper);

		server = new InsistenceLayerServer(serverManager, 0);
		server.start();

		remoteManager = new RemoteInsistenceLayer("localhost", server.getPort());
	}

	@After
	public void tearDown() throws Exception {
		if (remoteManager != null) remoteManager.close();
		if (server != null) server.stop();
		if (jdbcConnection != null) jdbcConnection.close();
	}

	@Test
	public void increaseCommandShouldIncreaseLevelOnServer() {
		remoteManager.increaseLevel();

		assertEquals(1, remoteManager.getCurrentLevel());
		assertEquals(1, serverManager.getCurrentLevel());
	}

	@Test
	public void fullLifecycleIncreaseResetDecrease() {
		remoteManager.increaseLevel();
		remoteManager.increaseLevel();
		assertEquals(2, remoteManager.getCurrentLevel());
		assertEquals(2, serverManager.getCurrentLevel());

		remoteManager.resetCurrentLevel();
		assertEquals(2, remoteManager.getCurrentLevel());
		assertEquals(2, serverManager.getCurrentLevel());

		remoteManager.decreaseLevel();
		assertEquals(1, remoteManager.getCurrentLevel());
		assertEquals(1, serverManager.getCurrentLevel());

		remoteManager.decreaseLevel();
		assertEquals(0, remoteManager.getCurrentLevel());
		assertEquals(0, serverManager.getCurrentLevel());
	}

	@Test
	public void decreaseAtLevelZeroShouldPropagateError() {
		assertEquals(0, remoteManager.getCurrentLevel());

		try {
			remoteManager.decreaseLevel();
			fail("should have thrown");
		} catch (InsistenceLayerException e) {
			assertEquals(0, remoteManager.getCurrentLevel());
		}
	}

	@Test
	public void remoteManagerWorksAsDropInReplacement() {
		InsistenceLayer manager = remoteManager;

		manager.increaseLevel();
		manager.increaseLevel();
		manager.increaseLevel();
		assertEquals(3, manager.getCurrentLevel());

		manager.resetCurrentLevel();
		assertEquals(3, manager.getCurrentLevel());

		manager.decreaseLevel();
		manager.decreaseLevel();
		manager.decreaseLevel();
		assertEquals(0, manager.getCurrentLevel());
	}

	@Test
	public void setLevelToShouldWorkRemotely() {
		remoteManager.setLevelTo(5);
		assertEquals(5, remoteManager.getCurrentLevel());
		assertEquals(5, serverManager.getCurrentLevel());

		remoteManager.setLevelTo(2);
		assertEquals(2, remoteManager.getCurrentLevel());
		assertEquals(2, serverManager.getCurrentLevel());

		remoteManager.setLevelTo(4);
		assertEquals(4, remoteManager.getCurrentLevel());
		assertEquals(4, serverManager.getCurrentLevel());
	}

	@Test
	public void decreaseAllLevelsShouldWorkRemotely() {
		remoteManager.setLevelTo(5);
		assertEquals(5, remoteManager.getCurrentLevel());

		remoteManager.decreaseAllLevels();
		assertEquals(0, remoteManager.getCurrentLevel());
		assertEquals(0, serverManager.getCurrentLevel());
	}

	@Test
	public void shouldThrowWhenServerIsDown() throws Exception {
		server.stop();

		try {
			remoteManager.increaseLevel();
			fail("should have thrown");
		} catch (InsistenceLayerException e) {
			assertEquals("Cannot connect to Insistence Layer server at localhost:" + server.getPort(), e.getMessage());
		}
	}

}
