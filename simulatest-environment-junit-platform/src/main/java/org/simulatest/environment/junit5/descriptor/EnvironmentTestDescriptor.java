package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import java.util.Objects;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Describes a single environment node in the test tree.
 * Runs the environment and manages Insistence Layer levels.
 */
public final class EnvironmentTestDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private final EnvironmentDefinition definition;
	private final boolean lastEnvironmentSibling;
	private boolean levelOpened;

	public EnvironmentTestDescriptor(UniqueId uniqueId, EnvironmentDefinition definition, boolean lastEnvironmentSibling) {
		super(uniqueId, Objects.requireNonNull(definition, "definition must not be null").getName());
		this.definition = definition;
		this.lastEnvironmentSibling = lastEnvironmentSibling;
	}

	public EnvironmentDefinition getDefinition() {
		return definition;
	}

	public boolean isLastEnvironmentSibling() {
		return lastEnvironmentSibling;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	@Override
	public SimulatestExecutionContext before(SimulatestExecutionContext context) {
		context.runEnvironment(definition);
		context.increaseInsistenceLevel();
		levelOpened = true;
		return context;
	}

	// The platform calls after() even when before() threw. A failed environment
	// opened no level, so decreasing here would close its parent's level instead.
	@Override
	public void after(SimulatestExecutionContext context) {
		if (levelOpened) context.decreaseInsistenceLevel();

		// Undoes what this environment wrote in the parent's level, also when it
		// failed halfway. Skipped for the last sibling: the parent's decreaseLevel
		// will roll back past this savepoint anyway, so resetting here would waste I/O.
		if (!lastEnvironmentSibling) {
			context.resetInsistenceLevel();
		}
	}

}
