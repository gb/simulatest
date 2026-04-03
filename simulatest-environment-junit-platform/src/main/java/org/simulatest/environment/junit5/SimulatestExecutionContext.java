package org.simulatest.environment.junit5;

import java.util.List;
import java.util.function.Consumer;

import org.junit.platform.engine.support.hierarchical.EngineExecutionContext;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.insistencelayer.InsistenceLayer;

public class SimulatestExecutionContext implements EngineExecutionContext {

	static final SimulatestExecutionContext EMPTY = new SimulatestExecutionContext(null);

	// Bridges the Simulatest engine context into Jupiter's internal execution so
	// that auto-detected extensions (InsistenceAfterEachExtension, etc.) can access it.
	private static final ThreadLocal<SimulatestExecutionContext> CURRENT = new ThreadLocal<>();

	private final SimulatestSession session;

	public SimulatestExecutionContext(SimulatestSession session) {
		this.session = session;
	}

	public InsistenceLayer insistenceLayer() {
		return session != null ? session.insistenceLayer() : null;
	}

	public EnvironmentFactory factory() {
		return session != null ? session.factory() : null;
	}

	public List<SimulatestPlugin> plugins() {
		return session != null ? session.plugins() : List.of();
	}

	public void runEnvironment(EnvironmentDefinition definition) {
		try {
			factory().create(definition).run();
		} catch (Exception exception) {
			throw new EnvironmentExecutionException(
					"Failed during run for environment '" + definition.getName() + "'", exception);
		}
	}

	public void increaseInsistenceLevel() {
		ifInsistenceLayer(InsistenceLayer::increaseLevel);
	}

	public void decreaseInsistenceLevel() {
		ifInsistenceLayer(InsistenceLayer::decreaseLevelOrCleanup);
	}

	public void resetInsistenceLevel() {
		ifInsistenceLayer(InsistenceLayer::resetCurrentLevel);
	}

	private void ifInsistenceLayer(Consumer<InsistenceLayer> action) {
		InsistenceLayer il = insistenceLayer();
		if (il != null) action.accept(il);
	}

	public void close() {
		if (session != null) session.close();
	}

	public static SimulatestExecutionContext getCurrent() {
		return CURRENT.get();
	}

	public static void setCurrent(SimulatestExecutionContext context) {
		CURRENT.set(context);
	}

	public static void clearCurrent() {
		CURRENT.remove();
	}

}
