package org.simulatest.environment.plugin;

import java.util.List;
import java.util.ServiceLoader;

/**
 * Discovers {@link SimulatestPlugin} implementations registered via
 * {@link ServiceLoader} in {@code META-INF/services}.
 */
public final class PluginDiscovery {

	private PluginDiscovery() {
	}

	/**
	 * Loads all registered plugin implementations from the classpath.
	 */
	public static List<SimulatestPlugin> loadPlugins() {
		return ServiceLoader.load(SimulatestPlugin.class).stream()
				.map(ServiceLoader.Provider::get)
				.toList();
	}

}
