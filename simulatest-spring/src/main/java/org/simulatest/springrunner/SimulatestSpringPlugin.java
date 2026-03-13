package org.simulatest.springrunner;

import java.util.Collection;

import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.springrunner.environment.EnvironmentSpringFactory;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;
import org.simulatest.springrunner.spring.SpringContext;

public class SimulatestSpringPlugin implements SimulatestPlugin {

	@Override
	public EnvironmentFactory environmentFactory() {
		return new EnvironmentSpringFactory();
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		testClasses.stream()
				.filter(c -> c.isAnnotationPresent(SimulatestSpringConfig.class))
				.findFirst()
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
