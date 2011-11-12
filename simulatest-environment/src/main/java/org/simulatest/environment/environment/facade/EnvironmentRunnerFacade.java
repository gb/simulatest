package org.simulatest.environment.environment.facade;

import java.util.ServiceLoader;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentRunner;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.infra.exception.EnvironmentGeneralException;

import com.google.common.base.Preconditions;

public class EnvironmentRunnerFacade {

	private EnvironmentFactory environmentFactory;
	private EnvironmentTreeBuilder builder = new EnvironmentTreeBuilder();

	public EnvironmentRunnerFacade() {
		ServiceLoader<EnvironmentFactory> loader = ServiceLoader.load(EnvironmentFactory.class);
		for (EnvironmentFactory factory : loader) environmentFactory = factory;
		assertEnvironmentFactoryNotNull();
	}
	
	public EnvironmentRunnerFacade(EnvironmentFactory environmentFactory) {
		Preconditions.checkNotNull(environmentFactory);
		this.environmentFactory = environmentFactory;
	}

	private void assertEnvironmentFactoryNotNull() {
		if (environmentFactory != null) return;
		throw new EnvironmentGeneralException("META-INF/services environmentFactory was not found!");
	}

	public void runEnvironment(Class<? extends Environment> environment) {
		runEnvironment(EnvironmentDefinition.create(environment));
	}
	
	public void runEnvironment(EnvironmentDefinition environmentDefinition) {
		builder.add(environmentDefinition);
		new EnvironmentRunner(environmentFactory, builder).run();
	}

}