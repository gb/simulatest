package org.simulatest.environment.junit5.test.testdouble;

import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.test.testdouble.environment.FirstLevelEnvironment;

@UseEnvironment(FirstLevelEnvironment.class)
public class ParallelProbeTest {

	private static final AtomicInteger ACTIVE = new AtomicInteger(0);
	private static final AtomicInteger MAX_ACTIVE = new AtomicInteger(0);

	public static void reset() {
		ACTIVE.set(0);
		MAX_ACTIVE.set(0);
	}

	public static int maxActive() {
		return MAX_ACTIVE.get();
	}

	@Test
	void first() throws InterruptedException {
		probe();
	}

	@Test
	void second() throws InterruptedException {
		probe();
	}

	@Test
	void third() throws InterruptedException {
		probe();
	}

	private void probe() throws InterruptedException {
		int current = ACTIVE.incrementAndGet();
		MAX_ACTIVE.accumulateAndGet(current, Math::max);
		Thread.sleep(120);
		ACTIVE.decrementAndGet();
	}

}
