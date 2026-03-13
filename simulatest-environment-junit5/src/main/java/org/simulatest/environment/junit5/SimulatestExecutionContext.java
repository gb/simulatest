package org.simulatest.environment.junit5;

import java.util.List;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayerManager;

public class SimulatestExecutionContext implements EngineExecutionContext {

	static final SimulatestExecutionContext EMPTY = new SimulatestExecutionContext(null, null, List.of());

	private final InsistenceLayerManager insistenceLayer;
	private final EnvironmentFactory factory;
	private final List<SimulatestPlugin> plugins;

	public SimulatestExecutionContext(InsistenceLayerManager insistenceLayer, EnvironmentFactory factory,
			List<SimulatestPlugin> plugins) {
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

	public List<SimulatestPlugin> plugins() {
		return plugins;
	}

}
