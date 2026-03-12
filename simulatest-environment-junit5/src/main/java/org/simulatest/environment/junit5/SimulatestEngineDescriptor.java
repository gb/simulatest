package org.simulatest.environment.junit5;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.junit5.plugin.SimulatestEnginePlugin;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;

/**
 * Root descriptor for the Simulatest test engine.
 * Loads plugins and creates the execution context in {@link #before}.
 */
class SimulatestEngineDescriptor extends EngineDescriptor implements Node<SimulatestExecutionContext> {

	private Collection<Class<?>> testClasses;

	SimulatestEngineDescriptor(UniqueId uniqueId) {
		super(uniqueId, "Simulatest");
	}

	void setTestClasses(Collection<Class<?>> testClasses) {
		this.testClasses = testClasses;
	}

	@Override
	public SimulatestExecutionContext before(SimulatestExecutionContext context) {
		if (testClasses == null || testClasses.isEmpty()) {
			return SimulatestExecutionContext.EMPTY;
		}

		List<SimulatestEnginePlugin> plugins = loadPlugins();

		for (SimulatestEnginePlugin plugin : plugins) {
			plugin.initialize(testClasses);
		}

		EnvironmentFactory factory = resolveEnvironmentFactory(plugins);

		InsistenceLayerManager insistenceLayer = null;
		if (InsistenceLayerDataSource.isConfigured()) {
			insistenceLayer = InsistenceLayerManagerFactory.build(
					InsistenceLayerDataSource.getDefault().getConnectionWrapper());
			insistenceLayer.increaseLevel();
		}

		return new SimulatestExecutionContext(insistenceLayer, factory, plugins);
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		if (context.insistenceLayer() != null) {
			context.insistenceLayer().decreaseLevel();
		}
		for (SimulatestEnginePlugin plugin : context.plugins()) {
			plugin.destroy();
		}
	}

	private List<SimulatestEnginePlugin> loadPlugins() {
		List<SimulatestEnginePlugin> plugins = new ArrayList<>();
		for (SimulatestEnginePlugin plugin : ServiceLoader.load(SimulatestEnginePlugin.class)) {
			plugins.add(plugin);
		}
		return plugins;
	}

	private EnvironmentFactory resolveEnvironmentFactory(List<SimulatestEnginePlugin> plugins) {
		for (SimulatestEnginePlugin plugin : plugins) {
			EnvironmentFactory factory = plugin.getEnvironmentFactory();
			if (factory != null) return factory;
		}
		return new EnvironmentReflectionFactory();
	}

}
