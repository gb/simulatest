package org.simulatest.jeerunner;

import java.lang.reflect.Field;
import java.util.Collection;

import jakarta.inject.Inject;

import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.environment.environment.TestInstantiationException;
import org.simulatest.jeerunner.cdi.CdiContext;
import org.simulatest.jeerunner.environment.EnvironmentCdiFactory;

public class SimulatestJakartaPlugin implements SimulatestPlugin {

	@Override
	public EnvironmentFactory environmentFactory() {
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
		for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Inject.class)) {
					inject(instance, field);
				}
			}
		}
	}

	private static void inject(Object instance, Field field) {
		field.setAccessible(true);
		try {
			field.set(instance, CdiContext.getBean(field.getType()));
		} catch (IllegalAccessException e) {
			throw new TestInstantiationException("Failed to inject CDI bean into field: " + field.getName(), e);
		}
	}

}
