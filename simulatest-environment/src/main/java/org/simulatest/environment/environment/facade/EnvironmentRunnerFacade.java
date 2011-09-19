package org.simulatest.environment.environment.facade;

import static org.simulatest.environment.environment.EnvironmentDefinition.create;

import java.util.ServiceLoader;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.infra.exception.EnvironmentGeneralException;

public class EnvironmentRunnerFacade {

	private EnvironmentFactory environmentFactory;

	public EnvironmentRunnerFacade() {
		ServiceLoader<EnvironmentFactory> loader = ServiceLoader.load(EnvironmentFactory.class);
		for (EnvironmentFactory factory : loader) environmentFactory = factory;
		assertEnvironmentFactoryNotNull();
	}

	private void assertEnvironmentFactoryNotNull() {
		if (environmentFactory != null) return;
		throw new EnvironmentGeneralException("META-INF/services environmentFactory was not found!");
	}

	public void runEnvironment(Class<? extends Environment> environment) {
		environmentFactory.create(create(environment)).run();
	}

}