package org.simulatest.springrunner.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.springrunner.spring.SpringContext;

public class EnvironmentSpringFactory implements EnvironmentFactory {
	
	@Override
	public Environment create(EnvironmentDefinition definition) {
		return SpringContext.getBean(definition.getEnvironmentClass());
	}

}