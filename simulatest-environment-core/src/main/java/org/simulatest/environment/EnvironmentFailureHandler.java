package org.simulatest.environment;

import org.simulatest.environment.infra.exception.EnvironmentExecutionException;

/**
 * Decides what happens when an environment fails to run.
 *
 * <p>The {@link EnvironmentRunner} never runs the tests or the child
 * environments of a failed environment. The handler decides the rest: when
 * it returns, the run goes on with the other environments; when it throws,
 * the run stops.</p>
 *
 * <p>Only the environment itself counts: being created and its {@code run()}.
 * A listener that fails is not a failed environment, and stops the run.</p>
 *
 * @see EnvironmentRunner#onEnvironmentFailure(EnvironmentFailureHandler)
 */
@FunctionalInterface
public interface EnvironmentFailureHandler {

	/**
	 * @param definition the environment that failed
	 * @param failure    what it failed with
	 */
	void environmentFailed(EnvironmentDefinition definition, EnvironmentExecutionException failure);

}
