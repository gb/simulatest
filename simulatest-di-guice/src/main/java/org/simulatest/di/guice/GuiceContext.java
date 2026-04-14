package org.simulatest.di.guice;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

import javax.sql.DataSource;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.Module;

import org.simulatest.environment.plugin.DependencyInjectionContext;

/**
 * <p><b>Thread-safety:</b> not thread-safe. Initialize and destroy from the
 * owning test thread; the Guice injector itself is thread-safe for reads.</p>
 */
public final class GuiceContext implements DependencyInjectionContext {

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

		SimulatestGuiceConfig config = DependencyInjectionContext
				.findConfigAnnotation(testClasses, SimulatestGuiceConfig.class)
				.orElseThrow(() -> new IllegalStateException(
						"No test class annotated with @SimulatestGuiceConfig found."));

		List<Module> modules = new ArrayList<>();

		for (Class<? extends Module> moduleClass : config.value()) {
			modules.add(instantiate(moduleClass, Module.class));
		}

		for (Class<? extends GuiceModuleProvider> providerClass : config.providers()) {
			modules.addAll(instantiate(providerClass, GuiceModuleProvider.class).modules());
		}

		injector = Guice.createInjector(modules);
	}

	@Override
	public Optional<DataSource> dataSource() {
		var binding = getInjector().getExistingBinding(Key.get(DataSource.class));
		return binding != null ? Optional.of(binding.getProvider().get()) : Optional.empty();
	}

	@Override
	public void destroy() {
		injector = null;
	}

	private Injector getInjector() {
		if (injector == null) {
			throw new IllegalStateException("Guice injector is not initialized. "
				+ "Add simulatest-di-guice to the classpath and annotate a test with @SimulatestGuiceConfig.");
		}
		return injector;
	}

	private static <T> T instantiate(Class<? extends T> clazz, Class<T> kind) {
		try {
			var constructor = clazz.getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (InvocationTargetException e) {
			throw new IllegalStateException(
					"Failed to instantiate " + kind.getSimpleName() + ": " + clazz.getName(),
					e.getCause() != null ? e.getCause() : e);
		} catch (InstantiationException | IllegalAccessException | NoSuchMethodException e) {
			throw new IllegalStateException(
					"Failed to instantiate " + kind.getSimpleName() + ": " + clazz.getName(), e);
		}
	}

}
