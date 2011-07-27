package org.simulatest.environment.environment.listener;


import org.apache.log4j.Logger;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentRunnerListener;
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
		insistenceLayerManager.increaseLevel();
	}

	@Override
	public void afterRun(EnvironmentDefinition definition) {
		logger.debug("[InsistenceListener] afterRun >> " + definition);
	}

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		logger.debug("[InsistenceListener] afterChildrenRun >> " + definition);
		insistenceLayerManager.decreaseLevel();
	}

}