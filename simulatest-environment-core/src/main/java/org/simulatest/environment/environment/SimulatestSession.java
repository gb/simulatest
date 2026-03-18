package org.simulatest.environment.environment;

import java.util.Collection;
import java.util.List;

import org.simulatest.insistencelayer.InsistenceLayer;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * Encapsulates the lifecycle of a Simulatest test run: plugin management,
 * factory resolution, and Insistence Layer discovery.
 *
 * <p>Both the JUnit 4 and JUnit 5 integrations open a session at the start
 * of a suite run and close it when the suite completes. This keeps the
 * orchestration logic in one place instead of duplicated across runners.</p>
 */
public class SimulatestSession implements AutoCloseable {

	private final List<SimulatestPlugin> plugins;
	private final EnvironmentFactory factory;
	private final InsistenceLayer insistenceLayer;

	private SimulatestSession(List<SimulatestPlugin> plugins, EnvironmentFactory factory,
							  InsistenceLayer insistenceLayer) {
		this.plugins = plugins;
		this.factory = factory;
		this.insistenceLayer = insistenceLayer;
	}

	/**
	 * Loads plugins via ServiceLoader, initializes them, resolves the factory
	 * and Insistence Layer. Typical entry point for JUnit 5.
	 */
	public static SimulatestSession open(Collection<Class<?>> testClasses) {
		List<SimulatestPlugin> plugins = SimulatestPlugins.loadAll();
		return open(plugins, testClasses);
	}

	/**
	 * Initializes pre-loaded plugins, resolves the factory and Insistence Layer.
	 * Used by JUnit 4 where plugins are loaded early (in the constructor) but
	 * initialized late (in {@code run()}).
	 */
	public static SimulatestSession open(List<SimulatestPlugin> plugins,
										 Collection<Class<?>> testClasses) {
		SimulatestPlugins.initializeAll(plugins, testClasses);
		return new SimulatestSession(
				plugins,
				SimulatestPlugins.resolveFactory(plugins),
				InsistenceLayerFactory.resolve());
	}

	public List<SimulatestPlugin> plugins() {
		return plugins;
	}

	public EnvironmentFactory factory() {
		return factory;
	}

	public InsistenceLayer insistenceLayer() {
		return insistenceLayer;
	}

	@Override
	public void close() {
		SimulatestPlugins.destroyAll(plugins);
	}

}
