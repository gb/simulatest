package org.simulatest.environment.environment;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;

public class SimulatestSessionTest {

	@Test
	public void openShouldInitializePluginsAndResolveFactory() {
		List<Collection<Class<?>>> receivedClasses = new ArrayList<>();
		EnvironmentFactory customFactory = definition -> null;
		SimulatestPlugin plugin = new SimulatestPlugin() {
			@Override
			public void initialize(Collection<Class<?>> testClasses) {
				receivedClasses.add(testClasses);
			}

			@Override
			public EnvironmentFactory environmentFactory() {
				return customFactory;
			}
		};

		Set<Class<?>> testClasses = Set.of(String.class);
		try (SimulatestSession session = SimulatestSession.open(List.of(plugin), testClasses)) {
			assertSame(customFactory, session.factory());
			assertSame(testClasses, receivedClasses.get(0));
		}
	}

	@Test
	public void openShouldFallBackToReflectionFactoryWhenNoPluginProvidesOne() {
		try (SimulatestSession session = SimulatestSession.open(List.of(), Set.of())) {
			assertTrue(session.factory() instanceof EnvironmentReflectionFactory);
		}
	}

	@Test
	public void closeShouldDestroyPlugins() {
		List<String> destroyed = new ArrayList<>();
		SimulatestPlugin plugin = new SimulatestPlugin() {
			@Override
			public void destroy() {
				destroyed.add("destroyed");
			}
		};

		SimulatestSession session = SimulatestSession.open(List.of(plugin), Set.of());
		assertTrue(destroyed.isEmpty());

		session.close();
		assertTrue(destroyed.contains("destroyed"));
	}

	@Test
	public void insistenceLayerShouldBeNullWhenNotConfigured() {
		try (SimulatestSession session = SimulatestSession.open(List.of(), Set.of())) {
			assertNull(session.insistenceLayer());
		}
	}

	@Test
	public void pluginsShouldBeAccessible() {
		SimulatestPlugin plugin = new SimulatestPlugin() {};
		List<SimulatestPlugin> plugins = List.of(plugin);

		try (SimulatestSession session = SimulatestSession.open(plugins, Set.of())) {
			assertNotNull(session.plugins());
			assertSame(plugin, session.plugins().get(0));
		}
	}

}
