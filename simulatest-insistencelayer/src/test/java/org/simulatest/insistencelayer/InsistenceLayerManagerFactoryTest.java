package org.simulatest.insistencelayer;

import static org.junit.Assert.*;

import org.junit.Test;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;

public class InsistenceLayerManagerFactoryTest {
	
	private ConnectionWrapper connectionWrapper = new ConnectionMock();
	
	@Test
	public void testSimpleCreate() {
		assertNotNull(InsistenceLayerManagerFactory.createInsistenceLayerManager(connectionWrapper));
	}
	
	@Test
	public void testCache() {
		InsistenceLayerManager instance1 = InsistenceLayerManagerFactory.createInsistenceLayerManager(connectionWrapper);
		InsistenceLayerManager instance2 = InsistenceLayerManagerFactory.createInsistenceLayerManager(connectionWrapper);
	
		assertTrue(instance1 == instance2);
	}

}
