package org.simulatest.insistencelayer;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

import org.junit.After;
import org.junit.Test;
import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;
import org.simulatest.insistencelayer.util.TestDataSources;

public class InsistenceLayerFactoryTest {

	@After
	public void cleanup() {
		InsistenceLayerFactory.clear();
	}

	@Test
	public void buildReturnsLocalInsistenceLayer() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		InsistenceLayer manager = InsistenceLayerFactory.build(wrapper);
		assertTrue(manager instanceof LocalInsistenceLayer);
	}

	@Test
	public void buildReturnsFreshInstanceEachTime() {
		ConnectionWrapper wrapper = newConnectionWrapper();
		InsistenceLayer first = InsistenceLayerFactory.build(wrapper);
		InsistenceLayer second = InsistenceLayerFactory.build(wrapper);

		assertNotSame(first, second);
	}

	@Test
	public void resolveReturnsNullWhenEmpty() {
		assertNull(InsistenceLayerFactory.resolve());
	}

	@Test
	public void resolveByNameReturnsNullWhenNotRegistered() {
		assertNull(InsistenceLayerFactory.resolve("nonexistent"));
	}

	@Test
	public void registerThenResolveByName() {
		InsistenceLayer manager = mock(InsistenceLayer.class);
		InsistenceLayerFactory.register("main", manager);

		assertSame(manager, InsistenceLayerFactory.resolve("main"));
	}

	@Test
	public void resolveReturnsFirstRegistered() {
		InsistenceLayer first = mock(InsistenceLayer.class);
		InsistenceLayer second = mock(InsistenceLayer.class);
		InsistenceLayerFactory.register("a", first);
		InsistenceLayerFactory.register("b", second);

		assertSame(first, InsistenceLayerFactory.resolve());
	}

	@Test
	public void deregisterRemovesEntry() {
		InsistenceLayer manager = mock(InsistenceLayer.class);
		InsistenceLayerFactory.register("main", manager);
		InsistenceLayerFactory.deregister("main");

		assertNull(InsistenceLayerFactory.resolve("main"));
	}

	@Test
	public void deregisterNonexistentNameDoesNothing() {
		InsistenceLayerFactory.deregister("nonexistent");
		assertNull(InsistenceLayerFactory.resolve());
	}

	@Test
	public void registerOverwritesPreviousValue() {
		InsistenceLayer first = mock(InsistenceLayer.class);
		InsistenceLayer second = mock(InsistenceLayer.class);

		InsistenceLayerFactory.register("main", first);
		InsistenceLayerFactory.register("main", second);

		assertSame(second, InsistenceLayerFactory.resolve("main"));
	}

	@Test
	public void clearRemovesAllEntries() {
		InsistenceLayerFactory.register("a", mock(InsistenceLayer.class));
		InsistenceLayerFactory.register("b", mock(InsistenceLayer.class));
		InsistenceLayerFactory.clear();

		assertNull(InsistenceLayerFactory.resolve());
		assertNull(InsistenceLayerFactory.resolve("a"));
		assertNull(InsistenceLayerFactory.resolve("b"));
	}

	@Test
	public void resolveFallsBackToConfiguredDataSourceAfterDeregister() {
		InsistenceLayerFactory.configure(TestDataSources.createH2("fallback-test"));
		InsistenceLayerFactory.deregister(InsistenceLayerFactory.DEFAULT);

		assertNotNull(InsistenceLayerFactory.resolve());
	}

	@Test
	public void configureOverwritesPreviousDataSource() {
		InsistenceLayerFactory.configure(TestDataSources.createH2("first"));
		var firstDataSource = InsistenceLayerFactory.dataSource();

		InsistenceLayerFactory.configure(TestDataSources.createH2("second"));

		assertNotSame(firstDataSource, InsistenceLayerFactory.dataSource());
	}

	@Test
	public void multipleNamesResolveIndependently() {
		InsistenceLayer first = mock(InsistenceLayer.class);
		InsistenceLayer second = mock(InsistenceLayer.class);

		InsistenceLayerFactory.register("orders", first);
		InsistenceLayerFactory.register("users", second);

		assertSame(first, InsistenceLayerFactory.resolve("orders"));
		assertSame(second, InsistenceLayerFactory.resolve("users"));
	}

	private static ConnectionWrapper newConnectionWrapper() {
		return new ConnectionWrapper(new ConnectionMock().getConnection());
	}

}
