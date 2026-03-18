package org.simulatest.environment.junit;

import java.util.List;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.environment.SimulatestSession;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;

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
		Object instance = SimulatestSession.createTestInstanceOrElse(plugins, getTestClass().getJavaClass(), this::newTestInstance);
		SimulatestSession.postProcessWithPlugins(plugins, instance);
		return instance;
	}

	private Object newTestInstance() {
		try {
			return super.createTest();
		} catch (Exception e) {
			throw new EnvironmentInstantiationException("Failed to create test instance: " + getTestClass().getName(), e);
		}
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
