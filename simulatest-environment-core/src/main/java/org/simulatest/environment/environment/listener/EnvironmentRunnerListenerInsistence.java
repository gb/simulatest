package org.simulatest.environment.environment.listener;

import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentRunnerListenerInsistence implements EnvironmentRunnerListener {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvironmentRunnerListenerInsistence.class);

	private final InsistenceLayer insistenceLayer;

	public EnvironmentRunnerListenerInsistence(InsistenceLayer insistenceLayer) {
		this.insistenceLayer = insistenceLayer;
	}

	@Override
	public void beforeRun(EnvironmentDefinition definition) {
		logger.debug("beforeRun >> {}", definition);
	}

	@Override
	public void afterRun(EnvironmentDefinition definition) {
		logger.debug("afterRun >> {}", definition);
		insistenceLayer.increaseLevel();
	}

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		logger.debug("afterChildrenRun >> {}", definition);
		insistenceLayer.decreaseLevel();
	}

	@Override
	public void afterSiblingCleanup(EnvironmentDefinition definition) {
		logger.debug("afterSiblingCleanup >> {}", definition);
		insistenceLayer.resetCurrentLevel();
	}

	@Override
	public ListenerPhase getPhase() {
		return ListenerPhase.INFRASTRUCTURE;
	}

}
