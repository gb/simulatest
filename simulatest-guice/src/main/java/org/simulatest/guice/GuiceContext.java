package org.simulatest.guice;

import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.Collection;

import javax.sql.DataSource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import org.simulatest.environment.plugin.DependencyInjectionContext;

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

		Class<? extends Module>[] moduleClasses = DependencyInjectionContext
				.findConfigAnnotation(testClasses, SimulatestGuiceConfig.class).value();

		Module[] modules = Arrays.stream(moduleClasses)
				.map(GuiceContext::instantiate)
				.toArray(Module[]::new);

		injector = Guice.createInjector(modules);
	}

	@Override
	public DataSource dataSource() {
		var binding = getInjector().getExistingBinding(Key.get(DataSource.class));
		return binding != null ? binding.getProvider().get() : null;
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
			var constructor = moduleClass.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InstantiationException | IllegalAccessException |
				 InvocationTargetException | NoSuchMethodException e) {
			throw new IllegalStateException("Failed to instantiate Guice module: " + moduleClass.getName(), e);
		}
	}

}
