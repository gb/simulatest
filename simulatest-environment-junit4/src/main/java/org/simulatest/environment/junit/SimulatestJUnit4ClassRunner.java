package org.simulatest.environment.junit;

import java.util.List;
import java.util.Objects;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;

public final class SimulatestJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private final EnvironmentJUnitRunner runner;
	private final List<SimulatestPlugin> plugins;

	public SimulatestJUnit4ClassRunner(EnvironmentJUnitRunner runner, Class<?> clazz, List<SimulatestPlugin> plugins) throws InitializationError {
		super(clazz);
		this.runner = Objects.requireNonNull(runner, "runner must not be null");
		this.plugins = Objects.requireNonNull(plugins, "plugins must not be null");
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

	// Reset the Insistence Layer after each test so sibling tests start
	// with the same database state (the environment's savepoint).
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		try {
			super.runChild(method, notifier);
		} finally {
			runner.resetInsistenceLevel();
		}
	}

}
