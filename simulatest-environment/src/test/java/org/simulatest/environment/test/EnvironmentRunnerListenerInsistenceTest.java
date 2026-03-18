package org.simulatest.environment.test;

import static org.mockito.Mockito.*;
import static org.simulatest.environment.EnvironmentDefinition.*;

import java.sql.SQLException;

import org.junit.Before;
import org.junit.Test;

import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.EnvironmentRunner;
import org.simulatest.environment.tree.EnvironmentTreeBuilder;
import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.test.testdouble.Environments.ColaboradorEnvironment;
import org.simulatest.environment.test.testdouble.Environments.DummyEnvironment;
import org.simulatest.environment.test.testdouble.Environments.EmpresaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.ProjetoEnvironment;
import org.simulatest.insistencelayer.InsistenceLayer;

public class EnvironmentRunnerListenerInsistenceTest {
	
	private EnvironmentTreeBuilder builder;
	private EnvironmentFactory factory;
	private EnvironmentRunnerListener insistenceListener;
	private InsistenceLayer insistenceLayer;
	
	@Before
	public void setup() {
		builder = new EnvironmentTreeBuilder();
		factory = new EnvironmentReflectionFactory();
		insistenceLayer = mock(InsistenceLayer.class);
		insistenceListener = new EnvironmentRunnerListenerInsistence(insistenceLayer);
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

		verify(insistenceLayer, times(8)).increaseLevel();
		verify(insistenceLayer, times(8)).decreaseLevel();
	}

	@Test
	public void resetCurrentLevelShouldOnlyBeCalledWhenSiblingsExist() throws SQLException {
		listenerWithMultiplesEnvironmentTest();

		// Only PessoaFisica/PessoaJuridica are siblings, so resetCurrentLevel
		// should fire exactly once (to clean PessoaEnvironment's level between siblings)
		verify(insistenceLayer, times(1)).resetCurrentLevel();
	}

	@Test
	public void shouldStillDecreaseOnEnvironmentFailure() {
		builder.add(create(DummyEnvironment.class));

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(insistenceListener);

		try {
			runner.run();
		} catch (EnvironmentExecutionException expected) {
			// DummyEnvironment throws during run()
		}

		// Even though DummyEnvironment fails, afterRun fires for both BigBang
		// and DummyEnvironment (increaseLevel x2), and afterChildrenRun fires
		// during cleanup for both (decreaseLevel x2). The checkpoint stack
		// is fully unwound by the runner's exception safety.
		verify(insistenceLayer, times(2)).increaseLevel();
		verify(insistenceLayer, times(2)).decreaseLevel();
	}

}