package org.simulatest.environment.bootstrap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.bootstrap.FakePluginsForOrderingTest.FirstFake;
import org.simulatest.environment.bootstrap.FakePluginsForOrderingTest.SecondFake;
import org.simulatest.environment.plugin.SimulatestPlugin;

/**
 * Locks in the rule that {@link DatabaseBootstrapPlugin} runs last so any
 * DI plugin that calls {@code InsistenceLayerFactory.configure(...)} in
 * its own {@code initialize(...)} wins the DataSource race.
 */
public class DatabaseBootstrapPluginOrderingTest {

	@Test
	public void databaseBootstrapPluginShouldBeLastEvenWithMultipleNonBootstrapPlugins() {
		List<SimulatestPlugin> plugins = SimulatestSession.loadPlugins();

		assertTrue("expected at least three plugins on the test classpath, got: " + plugins,
				plugins.size() >= 3);
		assertTrue("expected FirstFake to be present, got: " + plugins,
				plugins.stream().anyMatch(p -> p instanceof FirstFake));
		assertTrue("expected SecondFake to be present, got: " + plugins,
				plugins.stream().anyMatch(p -> p instanceof SecondFake));

		SimulatestPlugin last = plugins.get(plugins.size() - 1);
		assertEquals("DatabaseBootstrapPlugin must be last so DI plugins run first",
				DatabaseBootstrapPlugin.class, last.getClass());

		long bootstrapCount = plugins.stream()
				.filter(p -> p instanceof DatabaseBootstrapPlugin)
				.count();
		assertEquals("only the single registered DatabaseBootstrapPlugin should appear",
				1, bootstrapCount);
	}

}
