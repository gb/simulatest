package org.simulatest.environment.infra;

/**
 * Collects exceptions across multiple best-effort operations so the first
 * failure is rethrown with subsequent ones attached as suppressed.
 *
 * <p>Use for cleanup or fan-out loops where every operation must be attempted
 * even if earlier ones throw: run each step through {@link #capture(Runnable)},
 * then call {@link #throwIfAny()} at the end. Not thread-safe.</p>
 */
public final class ExceptionAggregator {

	private RuntimeException first;

	public void capture(Runnable action) {
		try {
			action.run();
		} catch (RuntimeException e) {
			add(e);
		}
	}

	public void add(RuntimeException exception) {
		if (first == null) first = exception;
		else first.addSuppressed(exception);
	}

	public void throwIfAny() {
		if (first != null) throw first;
	}

	public <X extends Throwable> void throwIfAny(java.util.function.Function<RuntimeException, X> wrapper) throws X {
		if (first != null) throw wrapper.apply(first);
	}

}
