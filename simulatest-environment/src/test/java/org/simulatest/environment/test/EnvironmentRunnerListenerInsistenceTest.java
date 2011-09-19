package org.simulatest.environment.test;

import java.sql.SQLException;


import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.EnvironmentRunner;
import org.simulatest.environment.environment.EnvironmentRunnerListener;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.environment.mock.Environments.ColaboradorEnvironment;
import org.simulatest.environment.mock.Environments.EmpresaEnvironment;
import org.simulatest.environment.mock.Environments.ProjetoEnvironment;
import org.simulatest.insistencelayer.InsistenceLayerManager;

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