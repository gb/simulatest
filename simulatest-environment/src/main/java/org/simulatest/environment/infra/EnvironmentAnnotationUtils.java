package org.simulatest.environment.infra;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.Environment;

public class EnvironmentAnnotationUtils {
	
	private EnvironmentAnnotationUtils() { }
	
	public static Class<? extends Environment> extractEnvironment(Class<?> testClass) {
		if (testClass == BigBangEnvironment.class) return null;
		UseEnvironment annotation = testClass.getAnnotation(UseEnvironment.class);
		return annotation == null ? BigBangEnvironment.class : annotation.value();
	}
	
	public static Class<? extends Environment> extractEnvironmentParent(Class<? extends Environment> environmentClass) {
		EnvironmentParent annotation = environmentClass.getAnnotation(EnvironmentParent.class);
		return annotation == null ? BigBangEnvironment.class : annotation.value();
	}
	
}