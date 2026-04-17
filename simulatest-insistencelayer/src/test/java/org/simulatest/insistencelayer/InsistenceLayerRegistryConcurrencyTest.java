package org.simulatest.insistencelayer;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.junit.Test;
import org.simulatest.insistencelayer.infra.sql.ConnectionWrapper;
import org.simulatest.insistencelayer.mock.ConnectionMock;
import org.simulatest.insistencelayer.util.TestDataSources;

/**
 * Verifies the "unconditionally thread-safe" contract documented on
 * {@link InsistenceLayerRegistry}. The lazy-init fallback path in
 * {@code resolve()} is the riskiest: without synchronization, concurrent
 * callers could materialize duplicate layers. This test gates N threads
 * on a {@link CountDownLatch} so they race into that path together.
 */
public class InsistenceLayerRegistryConcurrencyTest {

	private static final int THREAD_COUNT = 32;
	private static final long TIMEOUT_SECONDS = 5;

	@Test
	public void concurrentResolveShouldReturnTheSameLazilyBuiltLayer() throws Exception {
		InsistenceLayerRegistry registry = new InsistenceLayerRegistry();
		registry.configure(TestDataSources.createH2("registry-concurrent-resolve"));

		List<InsistenceLayer> resolved = invokeConcurrently(THREAD_COUNT, () ->
				registry.resolve().orElseThrow(() ->
						new AssertionError("resolve() returned empty despite configured DataSource")));

		Set<InsistenceLayer> distinct = Collections.newSetFromMap(new IdentityHashMap<>());
		distinct.addAll(resolved);
		assertEquals("all threads must observe the same layer instance (lazy-init must not duplicate)",
				1, distinct.size());
	}

	@Test
	public void concurrentRegisterAndResolveShouldNotLoseWrites() throws Exception {
		InsistenceLayerRegistry registry = new InsistenceLayerRegistry();

		CountDownLatch gate = new CountDownLatch(1);
		ExecutorService pool = Executors.newFixedThreadPool(THREAD_COUNT);
		try {
			for (int i = 0; i < THREAD_COUNT; i++) {
				final String name = "layer-" + i;
				pool.submit(() -> {
					try {
						gate.await();
						registry.register(name, new LocalInsistenceLayer(
								new ConnectionWrapper(new ConnectionMock().getConnection())));
					} catch (InterruptedException e) {
						Thread.currentThread().interrupt();
					}
				});
			}
			gate.countDown();
			pool.shutdown();
			assertTrue("workers did not finish within " + TIMEOUT_SECONDS + "s",
					pool.awaitTermination(TIMEOUT_SECONDS, TimeUnit.SECONDS));
		} finally {
			if (!pool.isTerminated()) pool.shutdownNow();
		}

		int observed = 0;
		for (int i = 0; i < THREAD_COUNT; i++) {
			if (registry.resolve("layer-" + i).isPresent()) observed++;
		}
		assertEquals("every registered name must be resolvable; none lost to races",
				THREAD_COUNT, observed);
	}

	private static List<InsistenceLayer> invokeConcurrently(int threads,
			Callable<InsistenceLayer> action) throws Exception {
		CountDownLatch gate = new CountDownLatch(1);
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		try {
			List<Future<InsistenceLayer>> futures = new ArrayList<>(threads);
			for (int i = 0; i < threads; i++) {
				futures.add(pool.submit(() -> {
					gate.await();
					return action.call();
				}));
			}
			gate.countDown();

			List<InsistenceLayer> results = new ArrayList<>(threads);
			for (Future<InsistenceLayer> future : futures) {
				results.add(future.get(TIMEOUT_SECONDS, TimeUnit.SECONDS));
			}
			return results;
		} catch (Exception exception) {
			fail("concurrent invocation failed: " + exception);
			throw exception;
		} finally {
			pool.shutdownNow();
		}
	}

}
