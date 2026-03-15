package org.simulatest.environment.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.simulatest.environment.environment.EnvironmentDefinition.*;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.EnvironmentRunner;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.test.EnvironmentRunnerListenerLog;
import org.simulatest.environment.environment.listener.ListenerPhase;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.test.testdouble.Environments.ColaboradorEnvironment;
import org.simulatest.environment.test.testdouble.Environments.DummyEnvironment;
import org.simulatest.environment.test.testdouble.Environments.EmpresaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.ProjetoEnvironment;

public class EnvironmentRunnerTest {

	private EnvironmentTreeBuilder builder = new EnvironmentTreeBuilder();
	private EnvironmentFactory factory = new EnvironmentReflectionFactory();
	private EnvironmentRunnerListenerLog listenerLog = new EnvironmentRunnerListenerLog();

	@Test
	public void testListenerWithUniqueEnvironment() {
		builder.add(bigBang());

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerLog);
		runner.run();

		Object[] expectedLogs = new Object[] {
				"[BigBangEnvironment] beforeRun",
				"[BigBangEnvironment] afterRun",
				"[BigBangEnvironment] afterChildrenRun"
		};

		assertArrayEquals(expectedLogs, listenerLog.getLogs().toArray());
	}


	@Test
	public void testListenerWithMultiplesEnvironment() throws SQLException {
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));
		builder.add(create(ProjetoEnvironment.class));

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerLog);
		runner.run();

		Object[] expectedLogs = new Object[] {
				"[BigBangEnvironment] beforeRun",
				"[BigBangEnvironment] afterRun",
					"[Root] beforeRun",
					"[Root] afterRun",
						"[PessoaEnvironment] beforeRun",
						"[PessoaEnvironment] afterRun",
							"[PessoaFisicaEnvironment] beforeRun",
							"[PessoaFisicaEnvironment] afterRun",
								"[ColaboradorEnvironment] beforeRun",
								"[ColaboradorEnvironment] afterRun",
								"[ColaboradorEnvironment] afterChildrenRun",
							"[PessoaFisicaEnvironment] afterChildrenRun",
							"[PessoaEnvironment] afterSiblingCleanup",
							"[PessoaJuridicaEnvironment] beforeRun",
							"[PessoaJuridicaEnvironment] afterRun",
								"[EmpresaEnvironment] beforeRun",
								"[EmpresaEnvironment] afterRun",
									"[ProjetoEnvironment] beforeRun",
									"[ProjetoEnvironment] afterRun",
									"[ProjetoEnvironment] afterChildrenRun",
								"[EmpresaEnvironment] afterChildrenRun",
							"[PessoaJuridicaEnvironment] afterChildrenRun",
						"[PessoaEnvironment] afterChildrenRun",
					"[Root] afterChildrenRun",
				"[BigBangEnvironment] afterChildrenRun"
		};

		assertArrayEquals(expectedLogs, listenerLog.getLogs().toArray());
	}
	
	@Test
	public void subsequentListenersShouldFireEvenWhenAPreviousListenerThrows() {
		builder.add(bigBang());

		EnvironmentRunnerListenerLog secondListener = new EnvironmentRunnerListenerLog();

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(new EnvironmentRunnerListener() {
			@Override
			public void afterRun(EnvironmentDefinition definition) {
				throw new RuntimeException("listener failure");
			}
		});
		runner.addListener(secondListener);

		try {
			runner.run();
			fail("should propagate the listener exception");
		} catch (RuntimeException exception) {
			assertEquals("Failed during afterRun for environment 'BigBangEnvironment'",
					exception.getMessage());
			assertEquals("listener failure", exception.getCause().getMessage());
		}

		assertTrue("second listener should have received afterRun despite first listener throwing",
				secondListener.getLogs().contains("[BigBangEnvironment] afterRun"));
	}

	@Test
	public void infrastructureListenersShouldFireBeforeApplicationListeners() {
		builder.add(bigBang());

		List<String> firingOrder = new ArrayList<>();

		EnvironmentRunnerListener appListener = new EnvironmentRunnerListener() {
			@Override
			public void afterRun(EnvironmentDefinition definition) {
				firingOrder.add("APPLICATION");
			}
		};

		EnvironmentRunnerListener infraListener = new EnvironmentRunnerListener() {
			@Override
			public void afterRun(EnvironmentDefinition definition) {
				firingOrder.add("INFRASTRUCTURE");
			}

			@Override
			public ListenerPhase getPhase() {
				return ListenerPhase.INFRASTRUCTURE;
			}
		};

		// Add APPLICATION first, then INFRASTRUCTURE — should still fire infra first
		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(appListener);
		runner.addListener(infraListener);
		runner.run();

		assertEquals("INFRASTRUCTURE", firingOrder.get(0));
		assertEquals("APPLICATION", firingOrder.get(1));
	}

	@Test
	public void shouldThrowAnEnvironmentExecutionExceptionWhenSomethingWrongHappen() {
		builder.add(create(DummyEnvironment.class));
		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerLog);
		
		try {
			runner.run();
			fail("should throw an EnvironmentExecutionException");
		} catch (EnvironmentExecutionException exception) {
			String expectedMessage = "Failed during run for environment 'DummyEnvironment'";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
}