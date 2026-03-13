package org.simulatest.jeerunner.cdi;

import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;

public class CdiContext {

	private static SeContainer container;

	public static <T> T getBean(Class<T> clazz) {
		return getContainer().select(clazz).get();
	}

	public static void initialize() {
		if (container != null && container.isRunning()) return;
		container = SeContainerInitializer.newInstance().initialize();
	}

	public static void destroy() {
		if (container != null && container.isRunning()) {
			container.close();
			container = null;
		}
	}

	private static SeContainer getContainer() {
		if (container == null || !container.isRunning()) {
			throw new IllegalStateException("CDI container is not running. "
				+ "CDI container is not initialized. Add simulatest-jee to the classpath.");
		}
		return container;
	}

}
