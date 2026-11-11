package org.simulatest.environment.junit5.descriptor;

import java.util.Set;

import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.junit.platform.launcher.TestIdentifier;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * One test Jupiter discovered in a test class, known to the Simulatest engine
 * before anything runs.
 *
 * <p>It carries the tags and the source Jupiter gave the test, so the filters
 * of the launcher (tags, a single method asked for by the build tool) take it
 * out like any other test. What is left is what the class runs.</p>
 *
 * <p>The test itself is run by Jupiter, in the inner run of the class. This
 * descriptor reports what Jupiter said about it. For a container that makes
 * its tests while it runs (a parameterized or repeated test, a test factory)
 * it also reports each of those tests.</p>
 */
final class JupiterTestDescriptor extends AbstractTestDescriptor implements Node<SimulatestExecutionContext> {

	private static final String NEVER_RUN = "Jupiter did not run it";

	private final TestIdentifier discovered;
	private final JupiterOutcomes outcomes;

	JupiterTestDescriptor(UniqueId uniqueId, TestIdentifier discovered, JupiterOutcomes outcomes) {
		super(uniqueId, discovered.getDisplayName(), discovered.getSource().orElse(null));
		this.discovered = discovered;
		this.outcomes = outcomes;
	}

	UniqueId jupiterId() {
		return discovered.getUniqueIdObject();
	}

	@Override
	public Type getType() {
		return discovered.getType();
	}

	@Override
	public Set<TestTag> getTags() {
		return discovered.getTags();
	}

	@Override
	public boolean mayRegisterTests() {
		return discovered.isContainer();
	}

	@Override
	public SkipResult shouldBeSkipped(SimulatestExecutionContext context) {
		return outcomes.of(jupiterId())
				.map(JupiterOutcome::skipResult)
				.orElseGet(() -> SkipResult.skip(whyItNeverRan()));
	}

	@Override
	public SimulatestExecutionContext execute(SimulatestExecutionContext context,
			DynamicTestExecutor dynamicTestExecutor) throws Exception {
		for (JupiterOutcome test : outcomes.testsInside(jupiterId())) {
			dynamicTestExecutor.execute(TestResultDescriptor.addedTo(this, test));
		}
		outcome().rethrowFailure();
		return context;
	}

	// A test with no outcome is skipped before it gets here, so there is one.
	private JupiterOutcome outcome() {
		return outcomes.of(jupiterId()).orElseThrow();
	}

	private String whyItNeverRan() {
		return outcomes.ofNearestContainerAround(jupiterId())
				.flatMap(JupiterOutcome::whyNothingInsideRan)
				.orElse(NEVER_RUN);
	}

}
