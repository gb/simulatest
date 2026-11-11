package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import java.util.Objects;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.SetupLevel;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Describes a single environment node in the test tree.
 * Runs the environment and manages Insistence Layer levels.
 */
public final class EnvironmentTestDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private final EnvironmentDefinition definition;
	private final boolean lastEnvironmentSibling;
	private SetupLevel level = SetupLevel.withoutLayer();

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
		level = context.newSetupLevel();
		context.runEnvironment(definition);
		level.open();
		return context;
	}

	// The platform calls after() even when before() threw. A failed environment
	// opened no level, and the level knows that, so the level of the parent is
	// never closed in its place.
	//
	// The data of the last sibling is left for the parent to undo: the parent
	// rolls back past it anyway, so undoing it here would waste a round trip.
	@Override
	public void after(SimulatestExecutionContext context) {
		if (lastEnvironmentSibling) level.close();
		else level.closeUndoingSetup();
	}

}
