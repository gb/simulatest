package org.simulatest.insistencelayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Test;
import org.simulatest.insistencelayer.connection.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;

public class InsistenceLayerManagerFactoryTest {

	@After
	public void cleanup() {
		InsistenceLayerManagerFactory.clear();
	}

	@Test
	public void buildReturnsLocalInsistenceLayerManager() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		InsistenceLayerManager manager = InsistenceLayerManagerFactory.build(wrapper);
		assertTrue(manager instanceof LocalInsistenceLayerManager);
	}

	@Test
	public void buildReturnsFreshInstanceEachTime() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		InsistenceLayerManager first = InsistenceLayerManagerFactory.build(wrapper);
		InsistenceLayerManager second = InsistenceLayerManagerFactory.build(wrapper);

		assertNotSame(first, second);
	}

	@Test
	public void resolveReturnsNullWhenEmpty() {
		assertNull(InsistenceLayerManagerFactory.resolve());
	}

	@Test
	public void resolveByNameReturnsNullWhenNotRegistered() {
		assertNull(InsistenceLayerManagerFactory.resolve("nonexistent"));
	}

	@Test
	public void registerThenResolveByName() {
		InsistenceLayerManager manager = mock(InsistenceLayerManager.class);
		InsistenceLayerManagerFactory.register("main", manager);

		assertSame(manager, InsistenceLayerManagerFactory.resolve("main"));
	}

	@Test
	public void resolveReturnsFirstRegistered() {
		InsistenceLayerManager first = mock(InsistenceLayerManager.class);
		InsistenceLayerManager second = mock(InsistenceLayerManager.class);
		InsistenceLayerManagerFactory.register("a", first);
		InsistenceLayerManagerFactory.register("b", second);

		assertSame(first, InsistenceLayerManagerFactory.resolve());
	}

	@Test
	public void deregisterRemovesEntry() {
		InsistenceLayerManager manager = mock(InsistenceLayerManager.class);
		InsistenceLayerManagerFactory.register("main", manager);
		InsistenceLayerManagerFactory.deregister("main");

		assertNull(InsistenceLayerManagerFactory.resolve("main"));
	}

	@Test
	public void deregisterNonexistentNameDoesNothing() {
		InsistenceLayerManagerFactory.deregister("nonexistent");
		assertNull(InsistenceLayerManagerFactory.resolve());
	}

	@Test
	public void registerOverwritesPreviousValue() {
		InsistenceLayerManager first = mock(InsistenceLayerManager.class);
		InsistenceLayerManager second = mock(InsistenceLayerManager.class);

		InsistenceLayerManagerFactory.register("main", first);
		InsistenceLayerManagerFactory.register("main", second);

		assertSame(second, InsistenceLayerManagerFactory.resolve("main"));
	}

	@Test
	public void clearRemovesAllEntries() {
		InsistenceLayerManagerFactory.register("a", mock(InsistenceLayerManager.class));
		InsistenceLayerManagerFactory.register("b", mock(InsistenceLayerManager.class));
		InsistenceLayerManagerFactory.clear();

		assertNull(InsistenceLayerManagerFactory.resolve());
		assertNull(InsistenceLayerManagerFactory.resolve("a"));
		assertNull(InsistenceLayerManagerFactory.resolve("b"));
	}

	@Test
	public void resolveFallsBackToConfiguredDataSourceAfterDeregister() {
		InsistenceLayerManagerFactory.configure(TestDataSources.createH2("fallback-test"));
		InsistenceLayerManagerFactory.deregister(InsistenceLayerManagerFactory.DEFAULT);

		assertNotNull(InsistenceLayerManagerFactory.resolve());
	}

	@Test
	public void configureOverwritesPreviousDataSource() {
		InsistenceLayerManagerFactory.configure(TestDataSources.createH2("first"));
		var firstDataSource = InsistenceLayerManagerFactory.dataSource();

		InsistenceLayerManagerFactory.configure(TestDataSources.createH2("second"));

		assertNotSame(firstDataSource, InsistenceLayerManagerFactory.dataSource());
	}

	@Test
	public void multipleNamesResolveIndependently() {
		InsistenceLayerManager first = mock(InsistenceLayerManager.class);
		InsistenceLayerManager second = mock(InsistenceLayerManager.class);

		InsistenceLayerManagerFactory.register("orders", first);
		InsistenceLayerManagerFactory.register("users", second);

		assertSame(first, InsistenceLayerManagerFactory.resolve("orders"));
		assertSame(second, InsistenceLayerManagerFactory.resolve("users"));
	}

	private static ConnectionWrapper newConnectionWrapper() {
		return new ConnectionWrapper(new ConnectionMock().getConnection());
	}

}
