package org.simulatest.springrunner.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Declares how the Spring ApplicationContext should be initialized for Simulatest tests.
 *
 * <p>Place this annotation on your test class (or test harness superclass) to specify
 * XML configuration locations, Java {@code @Configuration} classes, or both.</p>
 *
 * <p>If neither {@link #locations} nor {@link #classes} is specified, the default
 * classpath XML file {@code simulatest-applicationContext.xml} is used for backward
 * compatibility.</p>
 *
 * <h3>Examples</h3>
 * <pre>
 * // XML configuration
 * &#64;SimulatestSpringConfig(locations = "my-applicationContext.xml")
 *
 * // Java configuration
 * &#64;SimulatestSpringConfig(classes = MyAppConfig.class)
 *
 * // Both (XML merged into the annotation-based context)
 * &#64;SimulatestSpringConfig(classes = MyAppConfig.class, locations = "extra-beans.xml")
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Inherited
public @interface SimulatestSpringConfig {

	/** Classpath XML configuration file locations. */
	String[] locations() default {};

	/** Java {@code @Configuration} classes. */
	Class<?>[] classes() default {};

}
