package org.simulatest.environment.test;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import static org.simulatest.environment.EnvironmentDefinition.bigBang;
import static org.simulatest.environment.EnvironmentDefinition.create;
import org.simulatest.environment.Environment;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.EnvironmentRunner;
import org.simulatest.environment.tree.EnvironmentTreeBuilder;
import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.listener.ListenerPhase;
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

	@Test
	public void subsequentListenersShouldFireEvenWhenAPreviousListenerThrowsInBeforeRun() {
		builder.add(bigBang());
		EnvironmentRunnerListenerLog secondListener = new EnvironmentRunnerListenerLog();

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerThrowingIn(Phase.BEFORE_RUN));
		runner.addListener(secondListener);

		EnvironmentExecutionException thrown = assertAggregatedFailure(runner);
		assertEquals("Failed during beforeRun for environment 'BigBangEnvironment'", thrown.getMessage());
		assertTrue("second listener should receive beforeRun despite first listener throwing",
				secondListener.getLogs().contains("[BigBangEnvironment] beforeRun"));
	}

	@Test
	public void subsequentListenersShouldFireEvenWhenAPreviousListenerThrowsInAfterChildrenRun() {
		builder.add(bigBang());
		EnvironmentRunnerListenerLog secondListener = new EnvironmentRunnerListenerLog();

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerThrowingIn(Phase.AFTER_CHILDREN_RUN));
		runner.addListener(secondListener);

		EnvironmentExecutionException thrown = assertAggregatedFailure(runner);
		assertEquals("Failed during afterChildrenRun for environment 'BigBangEnvironment'", thrown.getMessage());
		assertTrue("second listener should receive afterChildrenRun despite first listener throwing",
				secondListener.getLogs().contains("[BigBangEnvironment] afterChildrenRun"));
	}

	@Test
	public void subsequentListenersShouldFireEvenWhenAPreviousListenerThrowsInAfterSiblingCleanup() {
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));
		EnvironmentRunnerListenerLog secondListener = new EnvironmentRunnerListenerLog();

		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerThrowingIn(Phase.AFTER_SIBLING_CLEANUP));
		runner.addListener(secondListener);

		EnvironmentExecutionException thrown = assertAggregatedFailure(runner);
		assertTrue("error message should name the afterSiblingCleanup phase, was: " + thrown.getMessage(),
				thrown.getMessage().contains("afterSiblingCleanup"));
		assertTrue("second listener should receive afterSiblingCleanup despite first listener throwing",
				secondListener.getLogs().stream().anyMatch(s -> s.endsWith("afterSiblingCleanup")));
	}

	@Test
	public void parentAfterSiblingCleanupShouldFireEvenWhenAChildEnvironmentRunThrows() {
		EnvironmentFactory throwingOnFirstSibling = new ThrowingEnvironmentFactory(ColaboradorEnvironment.class);
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));

		EnvironmentRunner runner = new EnvironmentRunner(throwingOnFirstSibling, builder);
		runner.addListener(listenerLog);

		try {
			runner.run();
			fail("expected an EnvironmentExecutionException from the throwing child");
		} catch (EnvironmentExecutionException expected) {
			assertTrue("failure should name the failing child, was: " + expected.getMessage(),
					expected.getMessage().contains("ColaboradorEnvironment"));
		}

		assertTrue("parent's afterSiblingCleanup must fire so insistence level resets before next sibling; "
				+ "listener log was: " + listenerLog.getLogs(),
				listenerLog.getLogs().contains("[PessoaEnvironment] afterSiblingCleanup"));
	}

	@Test
	public void leafAfterChildrenRunShouldStillFireAfterAfterRunListenerThrows() {
		builder.add(bigBang());
		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerThrowingIn(Phase.AFTER_RUN));
		runner.addListener(listenerLog);

		EnvironmentExecutionException thrown = assertAggregatedFailure(runner);
		assertEquals("Failed during afterRun for environment 'BigBangEnvironment'", thrown.getMessage());

		List<String> logs = listenerLog.getLogs();
		assertTrue("afterRun should have fired on the log listener", logs.contains("[BigBangEnvironment] afterRun"));
		assertTrue("afterChildrenRun cleanup must still fire after afterRun listener threw; log was: " + logs,
				logs.contains("[BigBangEnvironment] afterChildrenRun"));
	}

	private static EnvironmentExecutionException assertAggregatedFailure(EnvironmentRunner runner) {
		try {
			runner.run();
			fail("expected an EnvironmentExecutionException aggregating the listener failure");
			throw new AssertionError("unreachable");
		} catch (EnvironmentExecutionException expected) {
			return expected;
		}
	}

	private enum Phase { BEFORE_RUN, AFTER_RUN, AFTER_CHILDREN_RUN, AFTER_SIBLING_CLEANUP }

	private static EnvironmentRunnerListener listenerThrowingIn(Phase phase) {
		RuntimeException failure = new RuntimeException("listener failure in " + phase);
		return new EnvironmentRunnerListener() {
			@Override
			public void beforeRun(EnvironmentDefinition definition) {
				if (phase == Phase.BEFORE_RUN) throw failure;
			}

			@Override
			public void afterRun(EnvironmentDefinition definition) {
				if (phase == Phase.AFTER_RUN) throw failure;
			}

			@Override
			public void afterChildrenRun(EnvironmentDefinition definition) {
				if (phase == Phase.AFTER_CHILDREN_RUN) throw failure;
			}

			@Override
			public void afterSiblingCleanup(EnvironmentDefinition definition) {
				if (phase == Phase.AFTER_SIBLING_CLEANUP) throw failure;
			}
		};
	}

	/** Test Stub factory that forces a specific environment class to throw from {@code run()}. */
	private static final class ThrowingEnvironmentFactory implements EnvironmentFactory {
		private final Class<? extends Environment> throwingClass;
		private final EnvironmentFactory delegate = new EnvironmentReflectionFactory();

		ThrowingEnvironmentFactory(Class<? extends Environment> throwingClass) {
			this.throwingClass = throwingClass;
		}

		@Override
		public Environment create(EnvironmentDefinition definition) {
			if (throwingClass.equals(definition.getEnvironmentClass())) {
				return () -> { throw new RuntimeException("simulated environment failure"); };
			}
			return delegate.create(definition);
		}
	}

}