package org.simulatest.di.jee;

import org.simulatest.environment.plugin.DependencyInjectionPlugin;

public class SimulatestJakartaPlugin extends DependencyInjectionPlugin {

	public SimulatestJakartaPlugin() {
		super(new CdiContext());
	}

}
