package org.simulatest.environment.junit5.descriptor;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

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
		if (definition.getEnvironmentClass() != BigBangEnvironment.class) {
			Environment env = context.factory().create(definition);
			env.run();
		}
		if (context.insistenceLayer() != null) {
			context.insistenceLayer().increaseLevel();
		}
		return context;
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		if (context.insistenceLayer() != null) {
			context.insistenceLayer().decreaseLevel();
			context.insistenceLayer().resetCurrentLevel();
		}
	}

}
