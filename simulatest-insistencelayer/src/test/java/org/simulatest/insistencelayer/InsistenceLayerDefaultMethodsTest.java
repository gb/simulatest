package org.simulatest.insistencelayer;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

/**
 * Exercises the default methods on {@link InsistenceLayer} against a
 * controllable Test Stub, so the exception-suppression contracts are
 * verified without a live JDBC connection.
 */
public class InsistenceLayerDefaultMethodsTest {

	@Test
	public void runIsolatedShouldDecreaseLevelOnNormalCompletion() {
		var layer = new RecordingLayer();

		layer.runIsolated(() -> { /* no-op */ });

		assertEquals("level restored after normal completion", 0, layer.getCurrentLevel());
		assertArrayEquals(new String[] { "increaseLevel", "decreaseLevel" },
				layer.calls.toArray());
	}

	@Test
	public void runIsolatedShouldRethrowPrimaryExceptionWithCleanupSuppressed() {
		RuntimeException primary = new RuntimeException("action failed");
		RuntimeException cleanup = new RuntimeException("cleanup failed");
		var layer = new RecordingLayer();
		layer.failSetLevelToZeroWith(cleanup);

		try {
			layer.runIsolated(() -> { throw primary; });
			fail("runIsolated should propagate the primary exception");
		} catch (RuntimeException thrown) {
			assertSame("primary exception identity preserved", primary, thrown);
			assertEquals("exactly one suppressed cleanup exception", 1, thrown.getSuppressed().length);
			assertSame("cleanup exception attached as suppressed", cleanup, thrown.getSuppressed()[0]);
		}
	}

	@Test
	public void runIsolatedShouldRethrowPrimaryWithoutSuppressedWhenCleanupSucceeds() {
		RuntimeException primary = new RuntimeException("action failed");
		var layer = new RecordingLayer();

		try {
			layer.runIsolated(() -> { throw primary; });
			fail("runIsolated should propagate the primary exception");
		} catch (RuntimeException thrown) {
			assertSame(primary, thrown);
			assertEquals("no suppressed exception when cleanup succeeds",
					0, thrown.getSuppressed().length);
			assertEquals("cleanup restored to level 0", 0, layer.getCurrentLevel());
		}
	}

	@Test
	public void decreaseLevelOrCleanupShouldJustDecreaseWhenItSucceeds() {
		var layer = new RecordingLayer();
		layer.increaseLevel();
		layer.increaseLevel();

		layer.decreaseLevelOrCleanup();

		assertEquals("decrease by one on success path", 1, layer.getCurrentLevel());
		assertEquals("no fallback cleanup invoked", 0, layer.setLevelToInvocations);
	}

	@Test
	public void decreaseLevelOrCleanupShouldRethrowOriginalWithFallbackSuppressed() {
		RuntimeException original = new RuntimeException("decreaseLevel failed");
		RuntimeException fallback = new RuntimeException("decreaseAllLevels failed");
		var layer = new RecordingLayer();
		layer.increaseLevel();
		layer.failDecreaseLevelWith(original);
		layer.failSetLevelToZeroWith(fallback);

		try {
			layer.decreaseLevelOrCleanup();
			fail("decreaseLevelOrCleanup should propagate the original exception");
		} catch (RuntimeException thrown) {
			assertSame("original exception identity preserved", original, thrown);
			assertEquals("exactly one suppressed fallback", 1, thrown.getSuppressed().length);
			assertSame("fallback exception attached as suppressed", fallback, thrown.getSuppressed()[0]);
		}
	}

	@Test
	public void decreaseLevelOrCleanupShouldRethrowOriginalWithoutSuppressedWhenFallbackSucceeds() {
		RuntimeException original = new RuntimeException("decreaseLevel failed");
		var layer = new RecordingLayer();
		layer.increaseLevel();
		layer.increaseLevel();
		layer.failDecreaseLevelWith(original);

		try {
			layer.decreaseLevelOrCleanup();
			fail("decreaseLevelOrCleanup should propagate the original exception");
		} catch (RuntimeException thrown) {
			assertSame(original, thrown);
			assertEquals("no suppressed exception when fallback succeeds",
					0, thrown.getSuppressed().length);
			assertEquals("fallback restored to level 0", 0, layer.getCurrentLevel());
		}
	}

	/**
	 * Test Stub for {@link InsistenceLayer} that records invocations and lets
	 * each test inject failures at specific operations. Using the real
	 * {@link LocalInsistenceLayer} here would require a live connection and
	 * would mix interface-default-method bugs with implementation bugs.
	 */
	private static final class RecordingLayer implements InsistenceLayer {
		private int level;
		private int setLevelToInvocations;
		private RuntimeException decreaseLevelFailure;
		private RuntimeException setLevelToZeroFailure;
		private final List<String> calls = new ArrayList<>();

		void failDecreaseLevelWith(RuntimeException exception) {
			this.decreaseLevelFailure = exception;
		}

		void failSetLevelToZeroWith(RuntimeException exception) {
			this.setLevelToZeroFailure = exception;
		}

		@Override
		public int getCurrentLevel() { return level; }

		@Override
		public void increaseLevel() {
			calls.add("increaseLevel");
			level++;
		}

		@Override
		public void decreaseLevel() {
			calls.add("decreaseLevel");
			if (decreaseLevelFailure != null) throw decreaseLevelFailure;
			if (level == 0) throw new IllegalStateException("already at level 0");
			level--;
		}

		@Override
		public void resetCurrentLevel() { calls.add("resetCurrentLevel"); }

		@Override
		public void setLevelTo(int target) {
			calls.add("setLevelTo(" + target + ")");
			if (target == 0) {
				setLevelToInvocations++;
				if (setLevelToZeroFailure != null) throw setLevelToZeroFailure;
			}
			level = target;
		}
	}

}
