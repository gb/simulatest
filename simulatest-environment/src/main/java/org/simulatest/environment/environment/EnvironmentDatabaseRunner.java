package org.simulatest.environment.environment;

import java.sql.SQLException;

import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.insistencelayer.InsistenceLayerManager;


public class EnvironmentDatabaseRunner extends EnvironmentRunner {

	public EnvironmentDatabaseRunner(EnvironmentFactory factory,	EnvironmentTreeBuilder builder) throws SQLException {
		super(factory, builder);
		this.addListener(new EnvironmentRunnerListenerInsistence(InsistenceLayerManager.getInstance()));
	}

}