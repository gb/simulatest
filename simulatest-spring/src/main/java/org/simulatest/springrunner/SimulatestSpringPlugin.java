package org.simulatest.springrunner;

import org.simulatest.environment.environment.DependencyInjectionPlugin;
import org.simulatest.springrunner.spring.SpringContext;

public class SimulatestSpringPlugin extends DependencyInjectionPlugin {

	public SimulatestSpringPlugin() {
		super(new SpringContext());
	}

}
