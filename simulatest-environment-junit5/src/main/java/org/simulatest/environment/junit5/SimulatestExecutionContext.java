package org.simulatest.environment.junit5;

import java.util.List;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit5.plugin.SimulatestEnginePlugin;
import org.simulatest.insistencelayer.InsistenceLayerManager;

/**
 * Execution context that flows through the TestDescriptor tree during execution.
 * Carries the Insistence Layer, environment factory, and loaded plugins.
 */
public class SimulatestExecutionContext implements EngineExecutionContext {

	static final SimulatestExecutionContext EMPTY = new SimulatestExecutionContext(null, null, List.of());

	private final InsistenceLayerManager insistenceLayer;
	private final EnvironmentFactory factory;
	private final List<SimulatestEnginePlugin> plugins;

	public SimulatestExecutionContext(InsistenceLayerManager insistenceLayer, EnvironmentFactory factory,
			List<SimulatestEnginePlugin> plugins) {
		this.insistenceLayer = insistenceLayer;
		this.factory = factory;
		this.plugins = plugins;
	}

	public InsistenceLayerManager insistenceLayer() {
		return insistenceLayer;
	}

	public EnvironmentFactory factory() {
		return factory;
	}

	public List<SimulatestEnginePlugin> plugins() {
		return plugins;
	}

}
