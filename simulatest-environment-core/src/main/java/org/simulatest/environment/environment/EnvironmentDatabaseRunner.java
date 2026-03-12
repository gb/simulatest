package org.simulatest.environment.environment;

import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;
import org.simulatest.environment.tree.Tree;

public class EnvironmentDatabaseRunner extends EnvironmentRunner {

	private InsistenceLayerManager insistenceLayer;
	private boolean initialized;

	public EnvironmentDatabaseRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		super(factory, environmentTree);
	}

	public EnvironmentDatabaseRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree,
			InsistenceLayerManager insistenceLayer) {
		super(factory, environmentTree);
		this.insistenceLayer = insistenceLayer;
	}

	@Override
	public void run() {
		ensureInitialized();
		insistenceLayer.increaseLevel();
		try {
			super.run();
			insistenceLayer.decreaseLevel();
		} catch (RuntimeException exception) {
			insistenceLayer.decreaseAllLevels();
			throw exception;
		}
	}

	public InsistenceLayerManager insistenceLayer() {
		return insistenceLayer;
	}

	private void ensureInitialized() {
		if (initialized) return;
		if (insistenceLayer == null) {
			insistenceLayer = InsistenceLayerManagerFactory.build(
				InsistenceLayerDataSource.getDefault().getConnectionWrapper()
			);
		}
		this.addListener(new EnvironmentRunnerListenerInsistence(insistenceLayer));
		initialized = true;
	}

}
