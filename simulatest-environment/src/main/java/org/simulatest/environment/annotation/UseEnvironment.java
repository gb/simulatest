package org.simulatest.environment.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.simulatest.environment.environment.Environment;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UseEnvironment {

	public Class<? extends Environment> value();
	
}