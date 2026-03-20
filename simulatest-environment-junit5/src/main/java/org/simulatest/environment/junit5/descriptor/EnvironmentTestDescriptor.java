package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.junit5.SimulatestExecutionContext;
import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * Describes a single environment node in the test tree.
 * Runs the environment and manages Insistence Layer levels.
 */
public class EnvironmentTestDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	private final EnvironmentDefinition definition;

	public EnvironmentTestDescriptor(UniqueId uniqueId, EnvironmentDefinition definition) {
		super(uniqueId, definition.getName());
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
		try {
			context.factory().create(definition).run();
		} catch (Exception exception) {
			throw new EnvironmentExecutionException(
					"Failed during run for environment '" + definition.getName() + "'", exception);
		}

		if (context.insistenceLayer() != null) {
			context.insistenceLayer().increaseLevel();
		}
		return context;
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		InsistenceLayer insistenceLayer = context.insistenceLayer();
		if (insistenceLayer == null) return;

		insistenceLayer.decreaseLevelOrCleanup();
		insistenceLayer.resetCurrentLevel();
	}

}
