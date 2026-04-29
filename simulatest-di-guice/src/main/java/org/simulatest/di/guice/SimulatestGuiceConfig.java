package org.simulatest.di.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Module;

/**
 * Marks a test class as the source of Guice modules for a test session.
 *
 * <p>Apply to one (or several) test classes in the run to declare the modules
 * Guice should install. Modules can be listed directly via {@link #value()}
 * or computed by a {@link GuiceModuleProvider} listed in {@link #providers()}.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SimulatestGuiceConfig {

	/** Module classes to install directly. Each must have a public no-arg constructor. */
	Class<? extends Module>[] value() default {};

	/** Provider classes whose {@link GuiceModuleProvider#modules()} contributes additional modules. */
	Class<? extends GuiceModuleProvider>[] providers() default {};

}
