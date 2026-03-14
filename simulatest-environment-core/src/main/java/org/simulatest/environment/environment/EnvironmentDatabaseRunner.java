package org.simulatest.environment.environment;

import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.insistencelayer.InsistenceLayerManager;
import org.simulatest.insistencelayer.InsistenceLayerManagerFactory;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;
import org.simulatest.environment.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentDatabaseRunner extends EnvironmentRunner {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentDatabaseRunner.class);

	private final InsistenceLayerManager insistenceLayer;

	public EnvironmentDatabaseRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		this(factory, environmentTree, InsistenceLayerManagerFactory.build(InsistenceLayerDataSource.getDefault().getConnectionWrapper()));
	}

	public EnvironmentDatabaseRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree, InsistenceLayerManager insistenceLayer) {
		super(factory, environmentTree);
		this.insistenceLayer = insistenceLayer;
		this.addListener(new EnvironmentRunnerListenerInsistence(insistenceLayer));
	}

	@Override
	public void run() {
		insistenceLayer.increaseLevel();

		try {
			super.run();
			insistenceLayer.decreaseLevel();
		} catch (RuntimeException exception) {
			try {
				insistenceLayer.decreaseAllLevels();
			} catch (RuntimeException cleanupException) {
				logger.error("Checkpoint cleanup failed after environment failure", cleanupException);
				exception.addSuppressed(cleanupException);
			}
			throw exception;
		}
	}

	public InsistenceLayerManager insistenceLayer() {
		return insistenceLayer;
	}

}
