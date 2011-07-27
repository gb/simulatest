package objectivelabs.environmentTestHarness.test;

import java.sql.SQLException;

import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectiveLabs.environmentTestHarness.environment.EnvironmentFactory;
import objectiveLabs.environmentTestHarness.environment.EnvironmentReflectionFactory;
import objectiveLabs.environmentTestHarness.environment.EnvironmentRunner;
import objectiveLabs.environmentTestHarness.environment.EnvironmentRunnerListener;
import objectiveLabs.environmentTestHarness.environment.EnvironmentTreeBuilder;
import objectiveLabs.environmentTestHarness.environment.listener.EnvironmentRunnerListenerInsistence;
import objectiveLabs.insistenceLayer.InsistenceLayerManager;
import objectivelabs.environmentTestHarness.mock.Environments.ColaboradorEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.EmpresaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.ProjetoEnvironment;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class EnvironmentRunnerListenerInsistenceTest {
	
	private EnvironmentTreeBuilder builder;
	private EnvironmentFactory factory;
	private EnvironmentRunnerListener insistenceListener;
	private InsistenceLayerManager insistenceLayerManager;
	
	@Before
	public void setup() {
		builder = new EnvironmentTreeBuilder();
		factory = new EnvironmentReflectionFactory();
		insistenceLayerManager = Mockito.mock(InsistenceLayerManager.class);
		insistenceListener = new EnvironmentRunnerListenerInsistence(insistenceLayerManager);
	}
	
	private void listenerWithMultiplesEnvironmentTest() {
		builder.add(EnvironmentDefinition.create(ColaboradorEnvironment.class));
		builder.add(EnvironmentDefinition.create(EmpresaEnvironment.class));
		builder.add(EnvironmentDefinition.create(ProjetoEnvironment.class));

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(insistenceListener);
		
		runner.run();
	}
	
	@Test
	public void testCallsToListenerInsistence() throws SQLException {
		listenerWithMultiplesEnvironmentTest();
		
		Mockito.verify(insistenceLayerManager, Mockito.times(8)).increaseLevel();
		Mockito.verify(insistenceLayerManager, Mockito.times(8)).decreaseLevel();
	}

}
