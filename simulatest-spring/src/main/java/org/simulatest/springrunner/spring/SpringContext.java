package org.simulatest.springrunner.spring;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext {

	private static AnnotationConfigApplicationContext context;

	public static <T> T getBean(Class<T> clazz) {
		return getContext().getBean(clazz);
	}

	public static <T> T createBean(Class<T> clazz) {
		return getContext().getAutowireCapableBeanFactory().createBean(clazz);
	}

	public static void autowire(Object instance) {
		getContext().getAutowireCapableBeanFactory().autowireBean(instance);
	}

	public static void initialize(Class<?>... configClasses) {
		if (context != null) return;
		if (configClasses == null || configClasses.length == 0) {
			throw new IllegalArgumentException(
				"At least one @Configuration class must be provided.");
		}
		context = new AnnotationConfigApplicationContext(configClasses);
	}

	public static void destroy() {
		if (context != null) {
			context.close();
			context = null;
		}
	}

	public static void initializeFromTestClass(Class<?> testClass) {
		SimulatestSpringConfig config = findConfig(testClass);

		if (config == null) {
			throw new IllegalStateException(
				"No @SimulatestSpringConfig found on " + testClass.getName()
				+ " or its superclasses.");
		}

		initialize(config.value());
	}

	private static AnnotationConfigApplicationContext getContext() {
		if (context == null) {
			throw new IllegalStateException("Spring context is not running. "
				+ "Use @RunWith(SimulatestSpringRunner.class) to bootstrap it.");
		}
		return context;
	}

	private static SimulatestSpringConfig findConfig(Class<?> clazz) {
		while (clazz != null) {
			SimulatestSpringConfig config = clazz.getAnnotation(SimulatestSpringConfig.class);
			if (config != null) return config;
			clazz = clazz.getSuperclass();
		}
		return null;
	}

}
