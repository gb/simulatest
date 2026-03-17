package org.simulatest.environment.environment;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.function.Supplier;

public final class SimulatestPlugins {

	private SimulatestPlugins() {
	}

	public static List<SimulatestPlugin> loadAll() {
		return ServiceLoader.load(SimulatestPlugin.class).stream()
				.map(ServiceLoader.Provider::get)
				.toList();
	}

	public static EnvironmentFactory resolveFactory(List<SimulatestPlugin> plugins) {
		return plugins.stream()
				.map(SimulatestPlugin::environmentFactory)
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(EnvironmentReflectionFactory::new);
	}

	public static void initializeAll(List<SimulatestPlugin> plugins, Collection<Class<?>> testClasses) {
		for (SimulatestPlugin plugin : plugins)
			plugin.initialize(testClasses);
	}

	public static void destroyAll(List<SimulatestPlugin> plugins) {
		RuntimeException firstException = null;
		for (SimulatestPlugin plugin : plugins) {
			try {
				plugin.destroy();
			} catch (RuntimeException e) {
				if (firstException == null) {
					firstException = e;
				} else {
					firstException.addSuppressed(e);
				}
			}
		}
		if (firstException != null) throw firstException;
	}

	public static Object createTestInstanceOrElse(List<SimulatestPlugin> plugins, Class<?> testClass,
												   Supplier<Object> fallback) {
		return plugins.stream()
				.map(plugin -> plugin.createTestInstance(testClass))
				.filter(Objects::nonNull)
				.findFirst()
				.orElseGet(fallback);
	}

	public static void postProcessAll(List<SimulatestPlugin> plugins, Object instance) {
		for (SimulatestPlugin plugin : plugins)
			plugin.postProcessTestInstance(instance);
	}

}
