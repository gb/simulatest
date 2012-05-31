package org.simulatest.environment.test;

import static org.mockito.Mockito.*;
import static org.simulatest.environment.environment.EnvironmentDefinition.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.EnvironmentRunner;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.environment.test.testdouble.Environments.ColaboradorEnvironment;
import org.simulatest.environment.test.testdouble.Environments.EmpresaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.ProjetoEnvironment;
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
		insistenceLayerManager = mock(InsistenceLayerManager.class);
		insistenceListener = new EnvironmentRunnerListenerInsistence(insistenceLayerManager);
	}
	
	private void listenerWithMultiplesEnvironmentTest() {
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));
		builder.add(create(ProjetoEnvironment.class));

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(insistenceListener);
		
		runner.run();
	}
	
	@Test
	public void testCallsToListenerInsistence() throws SQLException {
		listenerWithMultiplesEnvironmentTest();
		
		verify(insistenceLayerManager, times(8)).increaseLevel();
		verify(insistenceLayerManager, times(8)).decreaseLevel();
	}

}