package org.simulatest.environment.junit5.descriptor;

import java.util.Set;

import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestTag;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.launcher.TestIdentifier;

/**
 * One test or container of Jupiter, as the Simulatest engine shows it to the
 * launcher.
 *
 * <p>The ones Jupiter discovers in a test class are known before anything
 * runs. They carry the tags and the source Jupiter gave them, so the filters
 * of the launcher (tags, a single method asked for by the build tool) take
 * them out like any other test. What is left is what the class runs.</p>
 *
 * <p>The ones Jupiter makes while it runs (one invocation of a parameterized
 * or repeated test, a dynamic test or container) are added when Jupiter
 * registers them, inside the descriptor that made them.</p>
 *
 * <p>Jupiter runs them all, and {@link JupiterRunReporter} reports them.</p>
 */
final class JupiterTestDescriptor extends AbstractTestDescriptor {

	private final TestIdentifier jupiterTest;

	/**
	 * The unique id ends with the id Jupiter gave the test, so a later run can
	 * select the same test. The descriptor is not added to {@code parent}.
	 */
	JupiterTestDescriptor(TestDescriptor parent, TestIdentifier jupiterTest) {
		super(parent.getUniqueId().append(JupiterDelegatingClassDescriptor.TEST_SEGMENT, jupiterTest.getUniqueId()),
				jupiterTest.getDisplayName(),
				jupiterTest.getSource().or(parent::getSource).orElse(null));
		this.jupiterTest = jupiterTest;
	}

	UniqueId jupiterId() {
		return jupiterTest.getUniqueIdObject();
	}

	@Override
	public Type getType() {
		return jupiterTest.getType();
	}

	@Override
	public Set<TestTag> getTags() {
		return jupiterTest.getTags();
	}

	@Override
	public boolean mayRegisterTests() {
		return jupiterTest.isContainer();
	}

}
