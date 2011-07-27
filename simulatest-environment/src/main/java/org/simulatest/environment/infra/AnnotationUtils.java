package org.simulatest.environment.infra;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.Environment;


public class AnnotationUtils {
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Environment> extractEnvironment(Class<?> testClass) {
		if (testClass == BigBangEnvironment.class) return null;
		return (Class<? extends Environment>) getAnnotationMethodValue(testClass, UseEnvironment.class, "value", BigBangEnvironment.class);
	}
	
	@SuppressWarnings("unchecked")
	public static Class<? extends Environment> extractEnvironmentParent(Class<?> environmentClass) {
		if (environmentClass == BigBangEnvironment.class) return null;
		return (Class<? extends Environment>) getAnnotationMethodValue(environmentClass, EnvironmentParent.class, "value", BigBangEnvironment.class);
	}
	
	private static Object getAnnotationMethodValue(Class<?> clazz, Class<? extends Annotation> annotationClass, String methodName, Object nullable) {
		Annotation annotation = clazz.getAnnotation(annotationClass);
		if (annotation == null) return nullable;
		return invokeMethod(annotationClass, methodName, annotation);
	}
	
	private static Object invokeMethod(Class<?> clazz, String methodName, Object instance)  {
		try {
			Method method = clazz.getMethod(methodName, new Class<?>[] {});
			return method.invoke(instance, new Object[] {});
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
}