package org.simulatest.environment.junit5.testdouble;

import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;

/**
 * A cyclic {@code @EnvironmentParent} chain kept isolated so the main
 * SimulatestTestEngine's classpath scan does not build a tree from it and
 * fail suite discovery. Deliberately has no {@code @UseEnvironment} entry
 * point: the test drives the cycle detection through
 * {@code DeferredEnvironmentCoordinator.walkParentChain} directly.
 */
public final class CyclicEnvironmentFixtures {

	private CyclicEnvironmentFixtures() { }

	@EnvironmentParent(NodeB.class)
	public static class NodeA implements Environment {
		@Override public void run() { }
	}

	@EnvironmentParent(NodeA.class)
	public static class NodeB implements Environment {
		@Override public void run() { }
	}

}
