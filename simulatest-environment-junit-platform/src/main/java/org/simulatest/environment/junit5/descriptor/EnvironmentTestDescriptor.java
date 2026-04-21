package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.TestDescriptor;
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

	public EnvironmentTestDescriptor(UniqueId uniqueId, EnvironmentDefinition definition) {
		super(uniqueId, Objects.requireNonNull(definition, "definition must not be null").getName());
		this.definition = definition;
	}

	public EnvironmentDefinition getDefinition() {
		return definition;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	@Override
	public SimulatestExecutionContext before(SimulatestExecutionContext context) {
		context.lifecycle().onEnter(definition, context.asExecution());
		return context;
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		context.lifecycle().onExit(definition, context.asExecution());

		// Skip the redundant reset for the last sibling: the parent's decreaseLevel
		// will roll back past this savepoint anyway, so resetting here would waste I/O.
		if (!isLastEnvironmentSibling()) {
			context.resetInsistenceLevel();
		}
	}

	private boolean isLastEnvironmentSibling() {
		return getParent()
				.map(parent -> {
					TestDescriptor lastEnv = parent.getChildren().stream()
							.filter(EnvironmentTestDescriptor.class::isInstance)
							.reduce((first, last) -> last)
							.orElse(null);
					return lastEnv == this;
				})
				.orElse(true);
	}

}
