package org.simulatest.guice;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

import org.simulatest.environment.environment.DependencyInjectionContext;
import org.simulatest.environment.infra.AnnotationUtils;

public class GuiceContext implements DependencyInjectionContext {

	private Injector injector;

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return getInjector().getInstance(clazz);
	}

	@Override
	public void injectMembers(Object instance) {
		getInjector().injectMembers(instance);
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		if (injector != null) return;

		Class<? extends Module>[] moduleClasses = AnnotationUtils
				.findConfigAnnotation(testClasses, SimulatestGuiceConfig.class).value();

		Module[] modules = Arrays.stream(moduleClasses)
				.map(GuiceContext::instantiate)
				.toArray(Module[]::new);

		injector = Guice.createInjector(modules);
	}

	@Override
	public void destroy() {
		injector = null;
	}

	private Injector getInjector() {
		if (injector == null) {
			throw new IllegalStateException("Guice injector is not initialized. "
				+ "Add simulatest-guice to the classpath and annotate a test with @SimulatestGuiceConfig.");
		}
		return injector;
	}

	private static Module instantiate(Class<? extends Module> moduleClass) {
		try {
			return moduleClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException |
				 InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("Failed to instantiate Guice module: " + moduleClass.getName(), e);
		}
	}

}
