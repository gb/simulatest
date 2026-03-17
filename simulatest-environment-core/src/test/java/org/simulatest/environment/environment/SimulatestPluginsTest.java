package org.simulatest.environment.environment;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Test;

public class SimulatestPluginsTest {

	@Test
	public void resolveFactory_shouldFallBackToReflectionWhenNoPluginProvidesFactory() {
		List<SimulatestPlugin> plugins = List.of(new SimulatestPlugin() {});

		EnvironmentFactory factory = SimulatestPlugins.resolveFactory(plugins);

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

		EnvironmentFactory resolved = SimulatestPlugins.resolveFactory(List.of(
				new SimulatestPlugin() {},
				pluginWithFactory
		));

		assertSame(customFactory, resolved);
	}

	@Test
	public void initializeAll_shouldPassTestClassesToEveryPlugin() {
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

		SimulatestPlugins.initializeAll(List.of(plugin1, plugin2), classes);

		assertEquals(2, receivedClasses.size());
		assertSame(classes, receivedClasses.get(0));
		assertSame(classes, receivedClasses.get(1));
	}

	@Test
	public void destroyAll_shouldDestroyAllPluginsEvenWhenOneThrows() {
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
			SimulatestPlugins.destroyAll(List.of(failing, healthy, alsoFailing));
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

		Object result = SimulatestPlugins.createTestInstanceOrElse(
				List.of(returnsNull, returnsInstance, neverReached), String.class, () -> "fallback");

		assertSame(expected, result);
	}

	@Test
	public void createTestInstanceOrElse_shouldUseFallbackWhenNoPluginCreatesInstance() {
		Object fallback = "fallback-instance";

		Object result = SimulatestPlugins.createTestInstanceOrElse(
				List.of(new SimulatestPlugin() {}), String.class, () -> fallback);

		assertSame(fallback, result);
	}

	@Test
	public void postProcessAll_shouldCallEveryPlugin() {
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

		SimulatestPlugins.postProcessAll(List.of(plugin1, plugin2), testInstance);

		assertEquals(2, processed.size());
		assertSame(testInstance, processed.get(0));
		assertSame(testInstance, processed.get(1));
	}

}
