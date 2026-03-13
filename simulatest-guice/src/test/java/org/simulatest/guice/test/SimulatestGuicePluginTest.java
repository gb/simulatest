package org.simulatest.guice.test;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.environment.SimulatestPlugin;
import org.simulatest.environment.environment.SimulatestPlugins;
import org.simulatest.guice.SimulatestGuicePlugin;

class SimulatestGuicePluginTest {

	@Test
	void shouldLoadGuicePluginViaServiceLoader() {
		List<SimulatestPlugin> plugins = SimulatestPlugins.loadAll();
		Assertions.assertTrue(
			plugins.stream().anyMatch(p -> p instanceof SimulatestGuicePlugin),
			"GuicePlugin should be discovered via ServiceLoader"
		);
	}

}
