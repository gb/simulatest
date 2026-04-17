package org.simulatest.insistencelayer.remote;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
	private InsistenceLayer serverLayer;
	private InsistenceLayerServer server;
	private RemoteInsistenceLayer remoteLayer;

	@Before
	public void setup() throws SQLException, IOException {
		jdbcConnection = DriverManager.getConnection("jdbc:h2:mem:remote_test;DB_CLOSE_DELAY=-1");
		ConnectionWrapper wrapper = new ConnectionWrapper(jdbcConnection);
		serverLayer = InsistenceLayerFactory.build(wrapper);

		server = new InsistenceLayerServer(serverLayer, 0);
		server.start();

		remoteLayer = new RemoteInsistenceLayer("localhost", server.getPort());
	}

	@After
	public void tearDown() throws Exception {
		if (remoteLayer != null) remoteLayer.close();
		if (server != null) server.stop();
		if (jdbcConnection != null) jdbcConnection.close();
	}

	@Test
	public void increaseCommandShouldIncreaseLevelOnServer() {
		remoteLayer.increaseLevel();

		assertEquals(1, remoteLayer.getCurrentLevel());
		assertEquals(1, serverLayer.getCurrentLevel());
	}

	@Test
	public void fullLifecycleIncreaseResetDecrease() {
		remoteLayer.increaseLevel();
		remoteLayer.increaseLevel();
		assertEquals(2, remoteLayer.getCurrentLevel());
		assertEquals(2, serverLayer.getCurrentLevel());

		remoteLayer.resetCurrentLevel();
		assertEquals(2, remoteLayer.getCurrentLevel());
		assertEquals(2, serverLayer.getCurrentLevel());

		remoteLayer.decreaseLevel();
		assertEquals(1, remoteLayer.getCurrentLevel());
		assertEquals(1, serverLayer.getCurrentLevel());

		remoteLayer.decreaseLevel();
		assertEquals(0, remoteLayer.getCurrentLevel());
		assertEquals(0, serverLayer.getCurrentLevel());
	}

	@Test
	public void decreaseAtLevelZeroShouldPropagateError() {
		assertEquals(0, remoteLayer.getCurrentLevel());

		try {
			remoteLayer.decreaseLevel();
			fail("should have thrown");
		} catch (InsistenceLayerException e) {
			assertEquals(0, remoteLayer.getCurrentLevel());
		}
	}

	@Test
	public void remoteLayerWorksAsDropInReplacement() {
		InsistenceLayer layer = remoteLayer;

		layer.increaseLevel();
		layer.increaseLevel();
		layer.increaseLevel();
		assertEquals(3, layer.getCurrentLevel());

		layer.resetCurrentLevel();
		assertEquals(3, layer.getCurrentLevel());

		layer.decreaseLevel();
		layer.decreaseLevel();
		layer.decreaseLevel();
		assertEquals(0, layer.getCurrentLevel());
	}

	@Test
	public void setLevelToShouldWorkRemotely() {
		remoteLayer.setLevelTo(5);
		assertEquals(5, remoteLayer.getCurrentLevel());
		assertEquals(5, serverLayer.getCurrentLevel());

		remoteLayer.setLevelTo(2);
		assertEquals(2, remoteLayer.getCurrentLevel());
		assertEquals(2, serverLayer.getCurrentLevel());

		remoteLayer.setLevelTo(4);
		assertEquals(4, remoteLayer.getCurrentLevel());
		assertEquals(4, serverLayer.getCurrentLevel());
	}

	@Test
	public void decreaseAllLevelsShouldWorkRemotely() {
		remoteLayer.setLevelTo(5);
		assertEquals(5, remoteLayer.getCurrentLevel());

		remoteLayer.decreaseAllLevels();
		assertEquals(0, remoteLayer.getCurrentLevel());
		assertEquals(0, serverLayer.getCurrentLevel());
	}

	@Test
	public void shouldThrowWhenServerIsDown() throws Exception {
		server.stop();

		try {
			remoteLayer.increaseLevel();
			fail("should have thrown");
		} catch (InsistenceLayerException e) {
			assertTrue("error should describe connection failure, was: " + e.getMessage(),
					e.getMessage().contains("Cannot connect"));
		}
	}

}
