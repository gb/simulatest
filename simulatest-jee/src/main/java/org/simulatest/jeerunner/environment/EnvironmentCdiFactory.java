package org.simulatest.jeerunner.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.jeerunner.cdi.CdiContext;

public class EnvironmentCdiFactory implements EnvironmentFactory {

	@Override
	public Environment create(EnvironmentDefinition definition) {
		return CdiContext.getBean(definition.getEnvironmentClass());
	}

}
