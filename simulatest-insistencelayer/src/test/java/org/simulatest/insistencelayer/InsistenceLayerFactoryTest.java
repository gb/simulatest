package org.simulatest.insistencelayer;

import static org.junit.Assert.assertNotSame;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;
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
	public void resolveReturnsEmptyWhenEmpty() {
		assertTrue(InsistenceLayerFactory.resolve().isEmpty());
	}

	@Test
	public void resolveByNameReturnsEmptyWhenNotRegistered() {
		assertTrue(InsistenceLayerFactory.resolve("nonexistent").isEmpty());
	}

	@Test
	public void registerThenResolveByName() {
		InsistenceLayer manager = mock(InsistenceLayer.class);
		InsistenceLayerFactory.register("main", manager);

		assertSame(manager, InsistenceLayerFactory.resolve("main").orElseThrow());
	}

	@Test
	public void resolveReturnsFirstRegistered() {
		InsistenceLayer first = mock(InsistenceLayer.class);
		InsistenceLayer second = mock(InsistenceLayer.class);
		InsistenceLayerFactory.register("a", first);
		InsistenceLayerFactory.register("b", second);

		assertSame(first, InsistenceLayerFactory.resolve().orElseThrow());
	}

	@Test
	public void deregisterRemovesEntry() {
		InsistenceLayer manager = mock(InsistenceLayer.class);
		InsistenceLayerFactory.register("main", manager);
		InsistenceLayerFactory.deregister("main");

		assertTrue(InsistenceLayerFactory.resolve("main").isEmpty());
	}

	@Test
	public void deregisterNonexistentNameDoesNothing() {
		InsistenceLayerFactory.deregister("nonexistent");
		assertTrue(InsistenceLayerFactory.resolve().isEmpty());
	}

	@Test
	public void registerOverwritesPreviousValue() {
		InsistenceLayer first = mock(InsistenceLayer.class);
		InsistenceLayer second = mock(InsistenceLayer.class);

		InsistenceLayerFactory.register("main", first);
		InsistenceLayerFactory.register("main", second);

		assertSame(second, InsistenceLayerFactory.resolve("main").orElseThrow());
	}

	@Test
	public void clearRemovesAllEntries() {
		InsistenceLayerFactory.register("a", mock(InsistenceLayer.class));
		InsistenceLayerFactory.register("b", mock(InsistenceLayer.class));
		InsistenceLayerFactory.clear();

		assertTrue(InsistenceLayerFactory.resolve().isEmpty());
		assertTrue(InsistenceLayerFactory.resolve("a").isEmpty());
		assertTrue(InsistenceLayerFactory.resolve("b").isEmpty());
	}

	@Test
	public void resolveFallsBackToConfiguredDataSourceAfterDeregister() {
		InsistenceLayerFactory.configure(TestDataSources.createH2("fallback-test"));
		InsistenceLayerFactory.deregister(InsistenceLayerFactory.DEFAULT);

		assertTrue(InsistenceLayerFactory.resolve().isPresent());
	}

	@Test
	public void configureOverwritesPreviousDataSource() {
		InsistenceLayerFactory.configure(TestDataSources.createH2("first"));
		var firstDataSource = InsistenceLayerFactory.dataSource().orElseThrow();

		InsistenceLayerFactory.configure(TestDataSources.createH2("second"));

		assertNotSame(firstDataSource, InsistenceLayerFactory.dataSource().orElseThrow());
	}

	@Test
	public void multipleNamesResolveIndependently() {
		InsistenceLayer first = mock(InsistenceLayer.class);
		InsistenceLayer second = mock(InsistenceLayer.class);

		InsistenceLayerFactory.register("orders", first);
		InsistenceLayerFactory.register("users", second);

		assertSame(first, InsistenceLayerFactory.resolve("orders").orElseThrow());
		assertSame(second, InsistenceLayerFactory.resolve("users").orElseThrow());
	}

	private static ConnectionWrapper newConnectionWrapper() {
		return new ConnectionWrapper(new ConnectionMock().getConnection());
	}

}
