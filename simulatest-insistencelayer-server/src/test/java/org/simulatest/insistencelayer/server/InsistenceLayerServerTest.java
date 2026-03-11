package org.simulatest.insistencelayer.server;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.rmi.registry.LocateRegistry;

import org.junit.BeforeClass;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runners.MethodSorters;
import org.simulatest.insistencelayer.connection.ConnectionBean;
import org.simulatest.insistencelayer.server.infra.InsistenceLayerServerException;

@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class InsistenceLayerServerTest {

	private static RMIConnectionFactory rmiServer;

	@BeforeClass
	public static void startServer() throws Exception {
		InsistenceLayerServer.start();
		rmiServer = (RMIConnectionFactory) LocateRegistry.getRegistry(1099).lookup("InsistenceLayer");
	}

	@Test
	public void t1_serverShouldReturnAvailabilityWhenIsOnLine() throws Exception {
		assertTrue(InsistenceLayerServer.isAvailable());
	}

	@Test(expected = NullPointerException.class)
	public void t2_serverShouldThrowAnExceptionWhenTryGetAConnectionWithoutAConnectionBean() throws Exception {
		rmiServer.getConnection();
	}

	@Test
	public void t3_serverShouldAvailableConnectionFactoryViaRMI() throws Exception {
		rmiServer.registerConnectionBean(new ConnectionBean("org.h2.Driver", "jdbc:h2:~/.h2/test", "sa", ""));
		assertNotNull(rmiServer.getConnection());
	}

	@Test(expected = InsistenceLayerServerException.class)
	public void t4_serverShouldThrownAnInsistenceLayerServerExceptionWhenIsAlreadyUp() {
		InsistenceLayerServer.start();
	}

	@Test
	public void t5_serverShouldReturnUnavailabilityWhenIsOffLine() throws Exception {
		InsistenceLayerServer.shutdown();
		assertFalse(InsistenceLayerServer.isAvailable());
	}

	@Test(expected = InsistenceLayerServerException.class)
	public void t6_serverShouldThrownAnInsistenceLayerServerExceptionWhenIsAlreadyDown() throws Exception {
		InsistenceLayerServer.shutdown();
	}

}
