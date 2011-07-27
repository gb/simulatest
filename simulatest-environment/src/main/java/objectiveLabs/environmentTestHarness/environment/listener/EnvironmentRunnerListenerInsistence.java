package objectiveLabs.environmentTestHarness.environment.listener;

import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectiveLabs.environmentTestHarness.environment.EnvironmentRunnerListener;
import objectiveLabs.insistenceLayer.InsistenceLayerManager;

import org.apache.log4j.Logger;

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