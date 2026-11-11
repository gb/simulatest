package org.simulatest.environment.junit5;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.SetupLevel;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.junit5.descriptor.JupiterDelegatingClassDescriptor;

import java.util.List;
import java.util.Objects;

class SimulatestEngineDescriptor extends EngineDescriptor implements Node<SimulatestExecutionContext> {

	private SetupLevel outermostLevel = SetupLevel.withoutLayer();

	SimulatestEngineDescriptor(UniqueId uniqueId) {
		super(Objects.requireNonNull(uniqueId, "uniqueId must not be null"), "Simulatest");
	}

	@Override
	public SimulatestExecutionContext before(SimulatestExecutionContext context) {
		List<Class<?>> testClasses = testClassesLeft();
		if (testClasses.isEmpty()) return context;

		SimulatestExecutionContext withSession = context.withSession(SimulatestSession.open(testClasses));
		outermostLevel = withSession.newSetupLevel();
		outermostLevel.open();
		return withSession;
	}

	// The classes still in the tree when the run starts: a filter of the launcher
	// may have taken every test of a class out, and the class with them.
	private List<Class<?>> testClassesLeft() {
		return getDescendants().stream()
				.filter(JupiterDelegatingClassDescriptor.class::isInstance)
				.map(JupiterDelegatingClassDescriptor.class::cast)
				.<Class<?>>map(JupiterDelegatingClassDescriptor::testClass)
				.toList();
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		try {
			outermostLevel.close();
		} finally {
			context.close();
		}
	}

}
