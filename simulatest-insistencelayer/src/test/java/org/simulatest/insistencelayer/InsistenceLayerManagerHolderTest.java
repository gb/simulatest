package org.simulatest.insistencelayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Test;

public class InsistenceLayerManagerHolderTest {

	@After
	public void cleanup() {
		InsistenceLayerManagerHolder.clear();
	}

	@Test
	public void getReturnsNullByDefault() {
		assertNull(InsistenceLayerManagerHolder.get());
	}

	@Test
	public void setThenGetReturnsSameInstance() {
		InsistenceLayerManager manager = mock(InsistenceLayerManager.class);
		InsistenceLayerManagerHolder.set(manager);

		assertSame(manager, InsistenceLayerManagerHolder.get());
	}

	@Test
	public void clearResetsToNull() {
		InsistenceLayerManagerHolder.set(mock(InsistenceLayerManager.class));
		InsistenceLayerManagerHolder.clear();

		assertNull(InsistenceLayerManagerHolder.get());
	}

	@Test
	public void setOverwritesPreviousValue() {
		InsistenceLayerManager first = mock(InsistenceLayerManager.class);
		InsistenceLayerManager second = mock(InsistenceLayerManager.class);

		InsistenceLayerManagerHolder.set(first);
		InsistenceLayerManagerHolder.set(second);

		assertSame(second, InsistenceLayerManagerHolder.get());
	}

}
