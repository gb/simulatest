package org.simulatest.di.guice;

import org.simulatest.environment.plugin.DependencyInjectionPlugin;

public class SimulatestGuicePlugin extends DependencyInjectionPlugin {

	public SimulatestGuicePlugin() {
		super(new GuiceContext());
	}

}
