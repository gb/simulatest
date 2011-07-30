package org.simulatest.environment.environment;

import java.sql.SQLException;

import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.environment.infra.EnvironmentInstantiationException;
import org.simulatest.environment.tree.Tree;
import org.simulatest.insistencelayer.InsistenceLayerManager;

public class EnvironmentDatabaseRunner extends EnvironmentRunner {
	
	private InsistenceLayerManager insistenceLayerManager;

	public EnvironmentDatabaseRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		super(factory, environmentTree);
		this.addListener(new EnvironmentRunnerListenerInsistence(insistenceLayer()));
	}
	
	public InsistenceLayerManager insistenceLayer() {
		if (insistenceLayerManager == null) insistenceLayerManager = getInsistenceLayerManager();
		return insistenceLayerManager;
	}
	
	//TODO get the insistenceLayerManager over the connection and don't statically
	private InsistenceLayerManager getInsistenceLayerManager() {
		try {
			return InsistenceLayerManager.getInstance();
		} catch (SQLException exception) {
			String message = "Error trying get the instance of InsistenceLayer";
			throw new EnvironmentInstantiationException(message, exception);
		}
	}

}