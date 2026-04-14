package org.simulatest.di.jee;

import org.simulatest.environment.plugin.DependencyInjectionPlugin;

public final class SimulatestJakartaPlugin extends DependencyInjectionPlugin {

	public SimulatestJakartaPlugin() {
		super(new CdiContext());
	}

}
