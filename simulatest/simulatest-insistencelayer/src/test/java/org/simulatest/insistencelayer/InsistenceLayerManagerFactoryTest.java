package org.simulatest.insistencelayer;

import static org.junit.Assert.*;

import org.junit.Test;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;

public class InsistenceLayerManagerFactoryTest {
	
	private ConnectionWrapper connectionWrapper = new ConnectionMock();
	
	@Test
	public void testSimpleCreate() {
		assertNotNull(InsistenceLayerManagerFactory.build(connectionWrapper));
	}
	
	@Test
	public void testCache() {
		InsistenceLayerManager instance1 = InsistenceLayerManagerFactory.build(connectionWrapper);
		InsistenceLayerManager instance2 = InsistenceLayerManagerFactory.build(connectionWrapper);
	
		assertTrue(instance1 == instance2);
	}

}
