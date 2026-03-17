package org.simulatest.environment.junit5;

import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.EngineDescriptor;
import org.junit.platform.engine.support.hierarchical.Node;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.environment.environment.SimulatestPlugins;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

import java.util.Collection;
import java.util.List;
import java.util.Map;

class SimulatestEngineDescriptor extends EngineDescriptor implements Node<SimulatestExecutionContext> {

	private final Map<String, String> jupiterConfigurationParameters;
	private Collection<Class<?>> testClasses;

	SimulatestEngineDescriptor(UniqueId uniqueId, Map<String, String> jupiterConfigurationParameters) {
		super(uniqueId, "Simulatest");
		this.jupiterConfigurationParameters = Map.copyOf(jupiterConfigurationParameters);
	}

	void setTestClasses(Collection<Class<?>> testClasses) {
		this.testClasses = testClasses;
	}

	@Override
	public SimulatestExecutionContext before(SimulatestExecutionContext context) {
		if (testClasses == null || testClasses.isEmpty()) {
			return SimulatestExecutionContext.EMPTY;
		}

		List<SimulatestPlugin> plugins = SimulatestPlugins.loadAll();
		SimulatestPlugins.initializeAll(plugins, testClasses);

		InsistenceLayer insistenceLayer = InsistenceLayerFactory.resolve();
		if (insistenceLayer != null) {
			insistenceLayer.increaseLevel();
		}

		return new SimulatestExecutionContext(insistenceLayer, SimulatestPlugins.resolveFactory(plugins),
				plugins, jupiterConfigurationParameters);
	}

	@Override
	public void after(SimulatestExecutionContext context) {
		if (context.insistenceLayer() != null) context.insistenceLayer().decreaseLevel();
		SimulatestPlugins.destroyAll(context.plugins());
	}

}
