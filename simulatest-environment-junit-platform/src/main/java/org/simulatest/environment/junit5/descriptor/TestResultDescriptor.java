package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Something Jupiter reported that the Simulatest engine did not know before
 * the run: one invocation of a parameterized or repeated test, one dynamic
 * test, or a container that did not succeed.
 *
 * <p>Registered while the class runs, with the outcome Jupiter gave it. It
 * succeeds, is skipped, or throws the original failure, so the Simulatest
 * engine reports the same status.</p>
 */
final class TestResultDescriptor extends AbstractTestDescriptor implements Node<SimulatestExecutionContext> {

	private final JupiterOutcome outcome;

	private TestResultDescriptor(TestDescriptor parent, JupiterOutcome outcome) {
		super(idInside(parent, outcome), outcome.displayName(),
				outcome.identifier().getSource().or(parent::getSource).orElse(null));
		this.outcome = outcome;
	}

	/** Creates the descriptor of the outcome as a child of {@code parent}. */
	static TestResultDescriptor addedTo(TestDescriptor parent, JupiterOutcome outcome) {
		TestResultDescriptor descriptor = new TestResultDescriptor(parent, outcome);
		parent.addChild(descriptor);
		return descriptor;
	}

	// The id ends with the id Jupiter gave the test, so a later run can select
	// the same test.
	private static UniqueId idInside(TestDescriptor parent, JupiterOutcome outcome) {
		return parent.getUniqueId()
				.append(JupiterDelegatingClassDescriptor.TEST_SEGMENT, outcome.jupiterId().toString());
	}

	@Override
	public Type getType() {
		return Type.TEST;
	}

	@Override
	public SkipResult shouldBeSkipped(SimulatestExecutionContext context) {
		return outcome.skipResult();
	}

	@Override
	public SimulatestExecutionContext execute(SimulatestExecutionContext context,
			DynamicTestExecutor dynamicTestExecutor) throws Exception {
		outcome.rethrowFailure();
		return context;
	}

}
