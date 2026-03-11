package org.simulatest.springrunner.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.AbstractApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {

	private static ApplicationContext context;
	private static AbstractApplicationContext managedContext;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContext.context = context;
	}

	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}

	/**
	 * Initializes Spring from the given classpath XML locations.
	 */
	public static void initializeSpring(String... xmlLocations) {
		initializeSpring(xmlLocations, new Class<?>[0]);
	}

	/**
	 * Initializes Spring from the given Java {@code @Configuration} classes.
	 */
	public static void initializeSpring(Class<?>... configClasses) {
		initializeSpring(new String[0], configClasses);
	}

	/**
	 * Initializes Spring from the given XML locations and/or Java
	 * {@code @Configuration} classes. When both are provided, XML locations
	 * are imported into the annotation-based context.
	 */
	public static void initializeSpring(String[] xmlLocations, Class<?>[] configClasses) {
		boolean hasXml = xmlLocations != null && xmlLocations.length > 0;
		boolean hasClasses = configClasses != null && configClasses.length > 0;

		if (hasClasses && hasXml) {
			AnnotationConfigApplicationContext parent = new AnnotationConfigApplicationContext(configClasses);
			managedContext = new ClassPathXmlApplicationContext(xmlLocations, parent);
		} else if (hasClasses) {
			managedContext = new AnnotationConfigApplicationContext(configClasses);
		} else if (hasXml) {
			managedContext = new ClassPathXmlApplicationContext(xmlLocations);
		} else {
			throw new IllegalArgumentException(
				"At least one XML location or @Configuration class must be provided.");
		}
	}

	/**
	 * Reads {@link SimulatestSpringConfig} from the given test class (walking
	 * up the hierarchy) and initializes Spring accordingly.
	 *
	 * @throws IllegalStateException if no {@code @SimulatestSpringConfig} is found
	 */
	public static void initializeFromTestClass(Class<?> testClass) {
		SimulatestSpringConfig config = findConfig(testClass);

		if (config == null) {
			throw new IllegalStateException(
				"No @SimulatestSpringConfig found on " + testClass.getName()
				+ " or its superclasses. Annotate your test class or harness with"
				+ " @SimulatestSpringConfig to specify Spring configuration.");
		}

		initializeSpring(config.locations(), config.classes());
	}

	public static void destroy() {
		if (managedContext != null) {
			managedContext.close();
			managedContext = null;
		}
	}

	private static SimulatestSpringConfig findConfig(Class<?> clazz) {
		while (clazz != null) {
			SimulatestSpringConfig config = clazz.getAnnotation(SimulatestSpringConfig.class);
			if (config != null) {
				return config;
			}
			clazz = clazz.getSuperclass();
		}
		return null;
	}

}
