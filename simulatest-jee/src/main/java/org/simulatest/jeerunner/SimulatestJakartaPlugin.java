package org.simulatest.jeerunner;

import org.simulatest.environment.environment.DependencyInjectionPlugin;
import org.simulatest.jeerunner.cdi.CdiContext;

public class SimulatestJakartaPlugin extends DependencyInjectionPlugin {

	public SimulatestJakartaPlugin() {
		super(new CdiContext());
	}

}
