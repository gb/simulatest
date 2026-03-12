package org.simulatest.jeerunner.junit5;

import java.lang.reflect.Field;
import java.util.Collection;

import jakarta.inject.Inject;

import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.junit5.plugin.SimulatestEnginePlugin;
import org.simulatest.jeerunner.cdi.CdiContext;
import org.simulatest.jeerunner.environment.EnvironmentCdiFactory;

/**
 * SPI plugin that integrates the Simulatest JUnit 5 TestEngine with Jakarta CDI.
 *
 * <p>Bootstraps a CDI container (e.g., Weld SE), provides a CDI-backed
 * {@link EnvironmentFactory}, and looks up test instances from the container.
 * Test classes can use {@link CdiContext#getBean(Class)} directly for CDI lookups.</p>
 */
public class SimulatestJakartaPlugin implements SimulatestEnginePlugin {

	@Override
	public EnvironmentFactory getEnvironmentFactory() {
		return new EnvironmentCdiFactory();
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		CdiContext.initialize();
	}

	@Override
	public void destroy() {
		CdiContext.destroy();
	}

	@Override
	public void postProcessTestInstance(Object instance) {
		for (Field field : instance.getClass().getDeclaredFields()) {
			if (field.isAnnotationPresent(Inject.class)) {
				field.setAccessible(true);
				try {
					field.set(instance, CdiContext.getBean(field.getType()));
				} catch (IllegalAccessException e) {
					throw new RuntimeException("Failed to inject CDI bean into field: " + field.getName(), e);
				}
			}
		}
	}

}
