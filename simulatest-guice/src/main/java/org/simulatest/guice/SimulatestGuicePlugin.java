package org.simulatest.guice;

import org.simulatest.environment.environment.DependencyInjectionPlugin;

public class SimulatestGuicePlugin extends DependencyInjectionPlugin {

	public SimulatestGuicePlugin() {
		super(new GuiceContext());
	}

}
