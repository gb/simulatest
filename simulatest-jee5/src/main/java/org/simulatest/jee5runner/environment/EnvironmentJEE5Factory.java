package org.simulatest.jee5runner.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.jee5runner.jee5.JEE5Context;

public class EnvironmentJEE5Factory implements EnvironmentFactory {
	
	@Override
	public Environment create(EnvironmentDefinition definition) {
		return (Environment) JEE5Context.lookup(definition.getEnvironmentClass());
	}

}