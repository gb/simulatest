package org.simulatest.insistencelayer;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Test;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;

public class InsistenceLayerManagerFactoryTest {

	@After
	public void cleanup() {
		InsistenceLayerManagerFactory.clearCache();
	}

	@Test
	public void testSimpleCreate() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		assertNotNull(InsistenceLayerManagerFactory.build(wrapper));
	}

	@Test
	public void testBuildReturnsLocalInsistenceLayerManager() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		InsistenceLayerManager manager = InsistenceLayerManagerFactory.build(wrapper);
		assertTrue(manager instanceof LocalInsistenceLayerManager);
	}

	@Test
	public void testCache() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		InsistenceLayerManager instance1 = InsistenceLayerManagerFactory.build(wrapper);
		InsistenceLayerManager instance2 = InsistenceLayerManagerFactory.build(wrapper);

		assertSame(instance1, instance2);
	}

	@Test
	public void testDifferentConnectionsReturnDifferentManagers() {
		ConnectionWrapper wrapper1 = newConnectionWrapper();
		ConnectionWrapper wrapper2 = newConnectionWrapper();

		InsistenceLayerManager manager1 = InsistenceLayerManagerFactory.build(wrapper1);
		InsistenceLayerManager manager2 = InsistenceLayerManagerFactory.build(wrapper2);

		assertNotSame(manager1, manager2);
	}

	private static ConnectionWrapper newConnectionWrapper() {
		return new ConnectionWrapper(new ConnectionMock().getConnection());
	}

}
