package org.simulatest.di.guice;

import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.di.guice.SimulatestGuicePlugin;

class SimulatestGuicePluginTest {

	@Test
	void shouldLoadGuicePluginViaServiceLoader() {
		List<SimulatestPlugin> plugins = SimulatestSession.loadPlugins();
		Assertions.assertTrue(
			plugins.stream().anyMatch(p -> p instanceof SimulatestGuicePlugin),
			"GuicePlugin should be discovered via ServiceLoader"
		);
	}

}
