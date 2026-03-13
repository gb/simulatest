package org.simulatest.environment.infra;

import java.lang.annotation.Annotation;
import java.util.Collection;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.Environment;

public class AnnotationUtils {

	private AnnotationUtils() {
		// All static
	}

	public static <A extends Annotation> A findConfigAnnotation(Collection<Class<?>> testClasses, Class<A> annotationType) {
		return testClasses.stream()
				.filter(clazz -> clazz.isAnnotationPresent(annotationType))
				.findFirst()
				.map(clazz -> clazz.getAnnotation(annotationType))
				.orElseThrow(() -> new IllegalStateException("No test class annotated with @" + annotationType.getSimpleName() + " found."));
	}
	
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
