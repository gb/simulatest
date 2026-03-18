package org.simulatest.guice;

import org.simulatest.environment.environment.plugin.DependencyInjectionPlugin;

public class SimulatestGuicePlugin extends DependencyInjectionPlugin {

	public SimulatestGuicePlugin() {
		super(new GuiceContext());
	}

}
