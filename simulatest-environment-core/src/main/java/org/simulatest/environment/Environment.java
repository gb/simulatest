package org.simulatest.environment;

/**
 * A test fixture that sets up database state for a group of tests.
 *
 * <p>Environments form a tree via {@link EnvironmentParent}. Each environment
 * trusts its parent the same way a class trusts its superclass: the parent's
 * data is already present when the child runs.</p>
 *
 * @see EnvironmentParent
 */
public interface Environment {

	/**
	 * Called once during the environment tree walk to populate this
	 * environment's data (inserts, configuration, etc.).
	 */
	void run();

}