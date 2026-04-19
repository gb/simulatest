package org.simulatest.environment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.simulatest.environment.Environment;

/**
 * Binds a test class to the {@link Environment} it should run inside.
 *
 * <p>Before the first test method runs, the environment (and its ancestor chain
 * via {@link EnvironmentParent}) is instantiated and {@link Environment#run()}
 * is invoked. The resulting database state is shared by every test in the class
 * and isolated from sibling environments via Insistence Layer savepoints. Test
 * classes without this annotation default to
 * {@link org.simulatest.environment.BigBangEnvironment}.</p>
 *
 * @see EnvironmentParent
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface UseEnvironment {

	/** The environment this test class depends on. */
	Class<? extends Environment> value();

}