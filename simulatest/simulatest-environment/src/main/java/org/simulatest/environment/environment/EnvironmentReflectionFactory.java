package org.simulatest.environment.environment;

import org.apache.log4j.Logger;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;

public class EnvironmentReflectionFactory implements EnvironmentFactory {
	
	private static final Logger logger = Logger.getLogger(EnvironmentFactory.class);

	@Override
	public Environment create(EnvironmentDefinition definition) {
		try {
			logger.info("[ReflectionRunner] instanciation >> " + definition.getName());
			return definition.getEnvironmentClass().newInstance();
		} catch (Exception exception) {
			String message = "Error in instanciation of environment: " + definition.getName();
			throw new EnvironmentInstantiationException(message, exception);
		}
	}
	
}