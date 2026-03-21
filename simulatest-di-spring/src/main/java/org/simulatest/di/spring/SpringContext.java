package org.simulatest.di.spring;

import java.util.Collection;
import java.util.stream.Stream;

import javax.sql.DataSource;

import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.plugin.DependencyInjectionContext;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.insistencelayer.infra.sql.InsistenceLayerDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.NoSuchBeanDefinitionException;
import org.springframework.beans.factory.config.BeanPostProcessor;
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

		context = new AnnotationConfigApplicationContext();
		context.getBeanFactory().addBeanPostProcessor(new InsistenceLayerDataSourcePostProcessor());

		DependencyInjectionContext.findConfigAnnotation(testClasses, SimulatestSpringConfig.class)
				.map(SimulatestSpringConfig::value)
				.filter(classes -> classes.length > 0)
				.ifPresentOrElse(
						context::register,
						() -> context.scan(resolveBasePackage(testClasses))
				);

		context.refresh();
	}

	@Override
	public void destroy() {
		if (context != null) {
			try {
				context.close();
			} finally {
				context = null;
			}
		}
	}

	@Override
	public DataSource dataSource() {
		try {
			return getContext().getBean(DataSource.class);
		} catch (NoSuchBeanDefinitionException e) {
			return null;
		}
	}

	private AnnotationConfigApplicationContext getContext() {
		if (context == null) {
			throw new IllegalStateException("Spring context is not initialized. "
				+ "Add simulatest-di-spring to the classpath.");
		}
		return context;
	}

	private static String resolveBasePackage(Collection<Class<?>> testClasses) {
		String base = Stream.concat(
				testClasses.stream(),
				testClasses.stream()
						.filter(c -> c.isAnnotationPresent(UseEnvironment.class))
						.map(c -> c.getAnnotation(UseEnvironment.class).value())
		)
				.map(c -> c.getPackage().getName())
				.reduce(SpringContext::commonPrefix)
				.orElseThrow(() -> new IllegalStateException("No test classes found"));

		if (base.isEmpty()) {
			throw new IllegalStateException(
					"Test and environment classes share no common package — "
					+ "use @SimulatestSpringConfig to specify configuration explicitly.");
		}
		return base;
	}

	static String commonPrefix(String a, String b) {
		String[] partsA = a.split("\\.");
		String[] partsB = b.split("\\.");
		StringBuilder prefix = new StringBuilder();

		for (int i = 0; i < Math.min(partsA.length, partsB.length); i++) {
			if (!partsA[i].equals(partsB[i])) break;
			if (i > 0) prefix.append('.');
			prefix.append(partsA[i]);
		}

		return prefix.toString();
	}

	private static class InsistenceLayerDataSourcePostProcessor implements BeanPostProcessor {

		@Override
		public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
			if (bean instanceof DataSource ds && !(bean instanceof InsistenceLayerDataSource)) {
				InsistenceLayerFactory.configure(ds);
				return InsistenceLayerFactory.requireDataSource();
			}
			return bean;
		}
	}

}
