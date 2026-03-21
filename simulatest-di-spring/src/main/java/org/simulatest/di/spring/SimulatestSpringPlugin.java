package org.simulatest.di.spring;

import org.simulatest.environment.plugin.DependencyInjectionPlugin;

public class SimulatestSpringPlugin extends DependencyInjectionPlugin {

	public SimulatestSpringPlugin() {
		super(new SpringContext());
	}

}
