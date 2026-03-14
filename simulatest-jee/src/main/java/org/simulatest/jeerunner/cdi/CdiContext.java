package org.simulatest.jeerunner.cdi;

import java.lang.reflect.Field;
import java.util.Collection;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.inject.Inject;

import org.simulatest.environment.environment.DependencyInjectionContext;
import org.simulatest.environment.environment.TestInstantiationException;

public class CdiContext implements DependencyInjectionContext {

	private SeContainer container;

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return getContainer().select(clazz).get();
	}

	@Override
	public void injectMembers(Object instance) {
		for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
			for (Field field : clazz.getDeclaredFields()) {
				if (field.isAnnotationPresent(Inject.class)) {
					inject(instance, field);
				}
			}
		}
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		if (container != null && container.isRunning()) return;
		container = SeContainerInitializer.newInstance().initialize();
	}

	@Override
	public void destroy() {
		if (container != null && container.isRunning()) {
			try {
				container.close();
			} finally {
				container = null;
			}
		}
	}

	private SeContainer getContainer() {
		if (container == null || !container.isRunning()) {
			throw new IllegalStateException("CDI container is not running. "
				+ "Add simulatest-jee to the classpath.");
		}
		return container;
	}

	private void inject(Object instance, Field field) {
		field.setAccessible(true);
		try {
			field.set(instance, getInstance(field.getType()));
		} catch (IllegalAccessException e) {
			throw new TestInstantiationException("Failed to inject CDI bean into field: " + field.getName(), e);
		}
	}

}
