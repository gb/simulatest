package org.simulatest.environment.junit5;

import java.util.List;
import java.util.Map;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.insistencelayer.InsistenceLayer;

public class SimulatestExecutionContext implements EngineExecutionContext {

	static final SimulatestExecutionContext EMPTY = new SimulatestExecutionContext(null, null, List.of(), Map.of());

	private static final ThreadLocal<SimulatestExecutionContext> CURRENT = new ThreadLocal<>();

	private final InsistenceLayer insistenceLayer;
	private final EnvironmentFactory factory;
	private final List<SimulatestPlugin> plugins;
	private final Map<String, String> jupiterConfigurationParameters;

	public SimulatestExecutionContext(InsistenceLayer insistenceLayer, EnvironmentFactory factory,
			List<SimulatestPlugin> plugins, Map<String, String> jupiterConfigurationParameters) {
		this.insistenceLayer = insistenceLayer;
		this.factory = factory;
		this.plugins = plugins;
		this.jupiterConfigurationParameters = jupiterConfigurationParameters;
	}

	public InsistenceLayer insistenceLayer() {
		return insistenceLayer;
	}

	public EnvironmentFactory factory() {
		return factory;
	}

	public List<SimulatestPlugin> plugins() {
		return plugins;
	}

	public Map<String, String> jupiterConfigurationParameters() {
		return jupiterConfigurationParameters;
	}

	public static SimulatestExecutionContext getCurrent() {
		return CURRENT.get();
	}

	public static void setCurrent(SimulatestExecutionContext context) {
		CURRENT.set(context);
	}

	public static void clearCurrent() {
		CURRENT.remove();
	}

}
