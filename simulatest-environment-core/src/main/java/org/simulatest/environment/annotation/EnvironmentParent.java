package org.simulatest.environment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.simulatest.environment.Environment;

/**
 * Declares the parent of an {@link Environment} in the environment tree.
 *
 * <p>An environment trusts its parent the same way a class trusts its superclass:
 * the parent runs first and its data is visible to the child. Environments without
 * this annotation are attached directly under the root
 * ({@link org.simulatest.environment.BigBangEnvironment}). Cycles in the parent
 * chain are rejected at tree-build time with
 * {@link org.simulatest.environment.infra.exception.EnvironmentCyclicException}.</p>
 *
 * @see UseEnvironment
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface EnvironmentParent {

	/** The parent environment class. */
	Class<? extends Environment> value();

}