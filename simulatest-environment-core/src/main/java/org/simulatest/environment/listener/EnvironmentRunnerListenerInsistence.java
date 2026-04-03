package org.simulatest.environment.listener;

import java.util.Objects;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.insistencelayer.InsistenceLayer;

public class EnvironmentRunnerListenerInsistence implements EnvironmentRunnerListener {

	private final InsistenceLayer insistenceLayer;

	public EnvironmentRunnerListenerInsistence(InsistenceLayer insistenceLayer) {
		this.insistenceLayer = Objects.requireNonNull(insistenceLayer, "insistenceLayer must not be null");
	}

	@Override
	public void afterRun(EnvironmentDefinition definition) {
		insistenceLayer.increaseLevel();
	}

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		insistenceLayer.decreaseLevelOrCleanup();
	}

	@Override
	public void afterSiblingCleanup(EnvironmentDefinition definition) {
		insistenceLayer.resetCurrentLevel();
	}

	@Override
	public ListenerPhase getPhase() {
		return ListenerPhase.INFRASTRUCTURE;
	}

}
