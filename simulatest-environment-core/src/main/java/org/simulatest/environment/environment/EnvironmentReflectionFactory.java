package org.simulatest.environment.environment;

import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentReflectionFactory implements EnvironmentFactory {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvironmentReflectionFactory.class);

	@Override
	public Environment create(EnvironmentDefinition definition) {
		try {
			logger.info("[ReflectionRunner] instantiation >> {}", definition.getName());
			var constructor = definition.getEnvironmentClass().getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (Exception exception) {
			String message = "Error in instantiation of environment: " + definition.getName();
			throw new EnvironmentInstantiationException(message, exception);
		}
	}
	
}