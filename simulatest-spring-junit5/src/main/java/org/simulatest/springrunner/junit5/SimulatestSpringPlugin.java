package org.simulatest.springrunner.junit5;

import java.util.Collection;

import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit5.plugin.SimulatestEnginePlugin;
import org.simulatest.springrunner.environment.EnvironmentSpringFactory;
import org.simulatest.springrunner.spring.SpringContext;

/**
 * SPI plugin that integrates the Simulatest JUnit 5 TestEngine with Spring.
 *
 * <p>Initializes a Spring application context from {@code @SimulatestSpringConfig}
 * on the first discovered test class, provides a Spring-backed {@link EnvironmentFactory},
 * and autowires test instances.</p>
 */
public class SimulatestSpringPlugin implements SimulatestEnginePlugin {

	@Override
	public EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentSpringFactory();
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		testClasses.stream().findFirst()
				.ifPresent(SpringContext::initializeFromTestClass);
	}

	@Override
	public void destroy() {
		SpringContext.destroy();
	}

	@Override
	public void postProcessTestInstance(Object instance) {
		SpringContext.autowire(instance);
	}

}
