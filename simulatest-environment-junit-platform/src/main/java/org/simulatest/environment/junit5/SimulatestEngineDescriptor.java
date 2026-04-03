package org.simulatest.environment.junit5;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.SimulatestSession;

import java.util.Collection;

class SimulatestEngineDescriptor extends EngineDescriptor implements Node<SimulatestExecutionContext> {

	private final Collection<Class<?>> testClasses;

	SimulatestEngineDescriptor(UniqueId uniqueId, Collection<Class<?>> testClasses) {
		super(uniqueId, "Simulatest");
		this.testClasses = testClasses;
	}

	@Override
	public SimulatestExecutionContext before(SimulatestExecutionContext context) {
		if (testClasses == null || testClasses.isEmpty()) {
			return SimulatestExecutionContext.EMPTY;
		}

		SimulatestSession session = SimulatestSession.open(testClasses);

		SimulatestExecutionContext ctx = new SimulatestExecutionContext(session);
		ctx.increaseInsistenceLevel();
		return ctx;
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		try {
			context.decreaseInsistenceLevel();
		} finally {
			context.close();
		}
	}

}
