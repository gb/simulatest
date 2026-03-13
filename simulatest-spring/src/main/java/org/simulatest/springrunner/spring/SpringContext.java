package org.simulatest.springrunner.spring;

import java.util.Collection;

import org.simulatest.environment.environment.DependencyInjectionContext;
import org.simulatest.environment.infra.AnnotationUtils;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

public class SpringContext implements DependencyInjectionContext {

	private AnnotationConfigApplicationContext context;

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return getContext().getAutowireCapableBeanFactory().createBean(clazz);
	}

	@Override
	public void injectMembers(Object instance) {
		getContext().getAutowireCapableBeanFactory().autowireBean(instance);
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		if (context != null) return;

		Class<?>[] configClasses = AnnotationUtils
				.findConfigAnnotation(testClasses, SimulatestSpringConfig.class).value();

		if (configClasses.length == 0) {
			throw new IllegalArgumentException(
				"At least one @Configuration class must be provided.");
		}

		context = new AnnotationConfigApplicationContext(configClasses);
	}

	@Override
	public void destroy() {
		if (context != null) {
			context.close();
			context = null;
		}
	}

	private AnnotationConfigApplicationContext getContext() {
		if (context == null) {
			throw new IllegalStateException("Spring context is not initialized. "
				+ "Add simulatest-spring to the classpath and annotate a test with @SimulatestSpringConfig.");
		}
		return context;
	}

}
