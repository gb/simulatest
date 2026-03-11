package org.simulatest.environment.environment.listener;

import org.apache.log4j.Logger;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.insistencelayer.InsistenceLayerManager;

public class EnvironmentRunnerListenerInsistence implements EnvironmentRunnerListener {
	
	private Logger logger = Logger.getLogger(EnvironmentRunnerListenerInsistence.class);

	private InsistenceLayerManager insistenceLayerManager;

	public EnvironmentRunnerListenerInsistence(InsistenceLayerManager insistenceLayerManager) {
		this.insistenceLayerManager = insistenceLayerManager;
	}

	@Override
	public void beforeRun(EnvironmentDefinition definition) {
		logger.debug("[InsistenceListener] beforeRun >> " + definition);
	}

	@Override
	public void afterRun(EnvironmentDefinition definition) {
		logger.debug("[InsistenceListener] afterRun >> " + definition);
		insistenceLayerManager.increaseLevel();
	}

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		logger.debug("[InsistenceListener] afterChildrenRun >> " + definition);
		insistenceLayerManager.decreaseLevel();
	}

	@Override
	public void afterSiblingCleanup(EnvironmentDefinition definition) {
		logger.debug("[InsistenceListener] afterSiblingCleanup >> " + definition);
		insistenceLayerManager.resetCurrentLevel();
	}

	@Override
	public ListenerPhase getPhase() {
		return ListenerPhase.INFRASTRUCTURE;
	}

}
