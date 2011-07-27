package objectiveLabs.environmentTestHarness.environment;

import java.sql.SQLException;

import objectiveLabs.environmentTestHarness.environment.listener.EnvironmentRunnerListenerInsistence;
import objectiveLabs.insistenceLayer.InsistenceLayerManager;

public class EnvironmentDatabaseRunner extends EnvironmentRunner {

	public EnvironmentDatabaseRunner(EnvironmentFactory factory,	EnvironmentTreeBuilder builder) throws SQLException {
		super(factory, builder);
		this.addListener(new EnvironmentRunnerListenerInsistence(InsistenceLayerManager.getInstance()));
	}

}