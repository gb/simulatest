package org.simulatest.environment;

/**
 * Implicit root of every environment tree.
 *
 * <p>Any {@link Environment} without an {@link org.simulatest.environment.annotation.EnvironmentParent}
 * declaration is treated as a child of {@code BigBangEnvironment}, so the runner
 * always has a single root to walk from. Its {@link #run()} is a no-op:
 * the root exists for tree shape, not setup work.</p>
 */
public final class BigBangEnvironment implements Environment {

	@Override public void run() {
		// Let there be light
	}

}
