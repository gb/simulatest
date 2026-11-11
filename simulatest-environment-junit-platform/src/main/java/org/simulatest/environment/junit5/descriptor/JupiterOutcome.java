package org.simulatest.environment.junit5.descriptor;

import java.util.Optional;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestExecutionResult.Status;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.Node.SkipResult;
import org.junit.platform.launcher.TestIdentifier;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;

/**
 * What Jupiter reported for one test or container of the inner run: it
 * finished with a result, or it was skipped for a reason.
 */
record JupiterOutcome(TestIdentifier identifier, TestExecutionResult result, String skipReason) {

	static JupiterOutcome finished(TestIdentifier identifier, TestExecutionResult result) {
		return new JupiterOutcome(identifier, result, null);
	}

	static JupiterOutcome skipped(TestIdentifier identifier, String reason) {
		return new JupiterOutcome(identifier, null, reason);
	}

	UniqueId jupiterId() {
		return identifier.getUniqueIdObject();
	}

	String displayName() {
		return identifier.getDisplayName();
	}

	boolean isTest() {
		return identifier.isTest();
	}

	boolean isInside(UniqueId container) {
		return !jupiterId().equals(container) && jupiterId().hasPrefix(container);
	}

	boolean isSkipped() {
		return skipReason != null;
	}

	/** True when it finished and did not succeed: it failed or was aborted. */
	boolean didNotSucceed() {
		return !isSkipped() && result.getStatus() != Status.SUCCESSFUL;
	}

	SkipResult skipResult() {
		return isSkipped() ? SkipResult.skip(skipReason) : SkipResult.doNotSkip();
	}

	/**
	 * For a container: says why Jupiter ran nothing of what is inside it, when
	 * the container is the cause. Jupiter reports a container that is disabled,
	 * failed or was aborted, and nothing for what is inside it.
	 */
	Optional<String> whyNothingInsideRan() {
		if (isSkipped()) return Optional.of(skipReason);
		if (result.getStatus() == Status.ABORTED) return Optional.of(displayName() + " was aborted");
		if (result.getStatus() == Status.FAILED) return Optional.of(displayName() + " failed");
		return Optional.empty();
	}

	/**
	 * Throws what the test or container failed or was aborted with, so the
	 * Simulatest engine reports the same status. Does nothing when it succeeded.
	 */
	void rethrowFailure() throws Exception {
		if (!didNotSucceed()) return;

		Throwable failure = result.getThrowable().orElseGet(this::failureWithoutThrowable);

		// Errors and Exceptions go out as they are, to keep their type. Any other
		// Throwable is wrapped, because a node may only throw an Exception.
		if (failure instanceof Exception exception) throw exception;
		if (failure instanceof Error error) throw error;
		throw new EnvironmentExecutionException("Test failed with unexpected throwable", failure);
	}

	private Throwable failureWithoutThrowable() {
		return new AssertionError("Test '" + displayName() + "' " + result.getStatus().name().toLowerCase());
	}

}
