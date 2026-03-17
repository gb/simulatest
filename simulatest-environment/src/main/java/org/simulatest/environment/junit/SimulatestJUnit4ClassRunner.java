package org.simulatest.environment.junit;

import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.environment.environment.SimulatestPlugins;

public class SimulatestJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private final AbstractEnvironmentJUnitRunner runner;
	private final List<SimulatestPlugin> plugins;

	public SimulatestJUnit4ClassRunner(AbstractEnvironmentJUnitRunner runner, Class<?> clazz, List<SimulatestPlugin> plugins) throws InitializationError {
		super(clazz);
		this.runner = runner;
		this.plugins = plugins;
	}

	@Override
	protected Object createTest() throws Exception {
		Object instance = SimulatestPlugins.createTestInstance(plugins, getTestClass().getJavaClass());
		if (instance == null) instance = super.createTest();
		SimulatestPlugins.postProcessAll(plugins, instance);
		return instance;
	}

	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		try {
			super.runChild(method, notifier);
		} finally {
			runner.resetInsistenceLevel();
		}
	}

}
