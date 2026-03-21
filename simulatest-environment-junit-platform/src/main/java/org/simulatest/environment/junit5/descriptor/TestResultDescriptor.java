package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * A dynamic test descriptor that replays a result captured from Jupiter's execution.
 *
 * <p>Created at runtime by {@link JupiterDelegatingClassDescriptor} after Jupiter
 * finishes running a test class. Each instance represents one leaf test
 * (a {@code @Test}, a single {@code @ParameterizedTest} invocation, a
 * {@code @RepeatedTest} repetition, etc.) and either succeeds or rethrows
 * the original failure so that the Simulatest engine reports the correct status.</p>
 */
class TestResultDescriptor extends AbstractTestDescriptor implements Node<SimulatestExecutionContext> {

	private final Throwable failure;
	private final String skipReason;

	private TestResultDescriptor(UniqueId uniqueId, String displayName, TestSource source,
			Throwable failure, String skipReason) {
		super(uniqueId, displayName, source);
		this.failure = failure;
		this.skipReason = skipReason;
	}

	static TestResultDescriptor fromResult(UniqueId id, String displayName,
			TestSource source, TestExecutionResult result) {
		Throwable throwable = null;
		if (result.getStatus() != TestExecutionResult.Status.SUCCESSFUL) {
			throwable = result.getThrowable()
					.orElse(new AssertionError("Test " + result.getStatus().name().toLowerCase()));
		}
		return new TestResultDescriptor(id, displayName, source, throwable, null);
	}

	static TestResultDescriptor skipped(UniqueId id, String displayName,
			TestSource source, String reason) {
		return new TestResultDescriptor(id, displayName, source, null, reason);
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	@Override
	public SkipResult shouldBeSkipped(SimulatestExecutionContext context) {
		return skipReason != null ? SkipResult.skip(skipReason) : SkipResult.doNotSkip();
	}

	@Override
	public SimulatestExecutionContext execute(SimulatestExecutionContext context,
			DynamicTestExecutor dynamicTestExecutor) throws Exception {
		if (failure instanceof Exception e) throw e;
		if (failure instanceof Error e) throw e;
		if (failure != null) throw new EnvironmentExecutionException("Test failed with unexpected throwable", failure);
		return context;
	}

}
