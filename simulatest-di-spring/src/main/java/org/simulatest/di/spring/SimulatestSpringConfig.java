package org.simulatest.di.spring;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SimulatestSpringConfig {

	Class<?>[] value() default {};

}
