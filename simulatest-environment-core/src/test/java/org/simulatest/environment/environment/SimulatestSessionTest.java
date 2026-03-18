package org.simulatest.environment.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.plugin.SimulatestPlugin;

public class SimulatestSessionTest {

	@Test
	public void resolveFactory_shouldFallBackToReflectionWhenNoPluginProvidesFactory() {
		List<SimulatestPlugin> plugins = List.of(new SimulatestPlugin() {});

		EnvironmentFactory factory = SimulatestSession.resolveFactory(plugins);

		assertTrue(factory instanceof EnvironmentReflectionFactory);
	}

	@Test
	public void resolveFactory_shouldUseFirstPluginFactory() {
		EnvironmentFactory customFactory = definition -> null;
		SimulatestPlugin pluginWithFactory = new SimulatestPlugin() {
			@Override
			public EnvironmentFactory environmentFactory() {
				return customFactory;
			}
		};

		EnvironmentFactory resolved = SimulatestSession.resolveFactory(List.of(
				new SimulatestPlugin() {},
				pluginWithFactory
		));

		assertSame(customFactory, resolved);
	}

	@Test
	public void initializePlugins_shouldPassTestClassesToEveryPlugin() {
		List<Collection<Class<?>>> receivedClasses = new ArrayList<>();
		SimulatestPlugin plugin1 = new SimulatestPlugin() {
			@Override
			public void initialize(Collection<Class<?>> testClasses) {
				receivedClasses.add(testClasses);
			}
		};
		SimulatestPlugin plugin2 = new SimulatestPlugin() {
			@Override
			public void initialize(Collection<Class<?>> testClasses) {
				receivedClasses.add(testClasses);
			}
		};
		Collection<Class<?>> classes = List.of(String.class, Integer.class);

		SimulatestSession.initializePlugins(List.of(plugin1, plugin2), classes);

		assertEquals(2, receivedClasses.size());
		assertSame(classes, receivedClasses.get(0));
		assertSame(classes, receivedClasses.get(1));
	}

	@Test
	public void destroyPlugins_shouldDestroyAllPluginsEvenWhenOneThrows() {
		List<String> destroyed = new ArrayList<>();
		SimulatestPlugin failing = new SimulatestPlugin() {
			@Override
			public void destroy() {
				destroyed.add("first");
				throw new RuntimeException("first failure");
			}
		};
		SimulatestPlugin healthy = new SimulatestPlugin() {
			@Override
			public void destroy() {
				destroyed.add("second");
			}
		};
		SimulatestPlugin alsoFailing = new SimulatestPlugin() {
			@Override
			public void destroy() {
				destroyed.add("third");
				throw new RuntimeException("third failure");
			}
		};

		RuntimeException thrown = null;
		try {
			SimulatestSession.destroyPlugins(List.of(failing, healthy, alsoFailing));
		} catch (RuntimeException e) {
			thrown = e;
		}

		assertEquals(List.of("first", "second", "third"), destroyed);
		assertNotNull(thrown);
		assertEquals("first failure", thrown.getMessage());
		assertEquals(1, thrown.getSuppressed().length);
		assertEquals("third failure", thrown.getSuppressed()[0].getMessage());
	}

	@Test
	public void createTestInstanceOrElse_shouldReturnFirstNonNullResult() {
		Object expected = "created-by-second";
		SimulatestPlugin returnsNull = new SimulatestPlugin() {};
		SimulatestPlugin returnsInstance = new SimulatestPlugin() {
			@Override
			public Object createTestInstance(Class<?> testClass) {
				return expected;
			}
		};
		SimulatestPlugin neverReached = new SimulatestPlugin() {
			@Override
			public Object createTestInstance(Class<?> testClass) {
				return "should-not-be-used";
			}
		};

		Object result = SimulatestSession.createTestInstanceOrElse(
				List.of(returnsNull, returnsInstance, neverReached), String.class, () -> "fallback");

		assertSame(expected, result);
	}

	@Test
	public void createTestInstanceOrElse_shouldUseFallbackWhenNoPluginCreatesInstance() {
		Object fallback = "fallback-instance";

		Object result = SimulatestSession.createTestInstanceOrElse(
				List.of(new SimulatestPlugin() {}), String.class, () -> fallback);

		assertSame(fallback, result);
	}

	@Test
	public void postProcessWithPlugins_shouldCallEveryPlugin() {
		List<Object> processed = new ArrayList<>();
		SimulatestPlugin plugin1 = new SimulatestPlugin() {
			@Override
			public void postProcessTestInstance(Object instance) {
				processed.add(instance);
			}
		};
		SimulatestPlugin plugin2 = new SimulatestPlugin() {
			@Override
			public void postProcessTestInstance(Object instance) {
				processed.add(instance);
			}
		};
		Object testInstance = new Object();

		SimulatestSession.postProcessWithPlugins(List.of(plugin1, plugin2), testInstance);

		assertEquals(2, processed.size());
		assertSame(testInstance, processed.get(0));
		assertSame(testInstance, processed.get(1));
	}

	// --- Session lifecycle tests ---

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
