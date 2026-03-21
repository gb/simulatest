package org.simulatest.di.guice;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.google.inject.Module;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SimulatestGuiceConfig {

	Class<? extends Module>[] value() default {};

	Class<? extends GuiceModuleProvider>[] providers() default {};

}
