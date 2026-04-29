package org.simulatest.di.spring;

import java.lang.annotation.*;

/**
 * Marks a test class as the source of Spring {@code @Configuration} classes
 * for a test session. When present, the test's {@link SpringContext}
 * registers the listed configurations; when absent, the context falls back
 * to component-scanning the packages of the discovered test classes.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SimulatestSpringConfig {

	/** Spring {@code @Configuration} (or component) classes to register. */
	Class<?>[] value() default {};

}
