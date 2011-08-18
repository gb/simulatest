package org.simulatest.environment.test.environment;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.sql.SQLException;

import org.junit.Test;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentFactory;
import org.simulatest.environment.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.environment.EnvironmentRunner;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.environment.listener.EnvironmentRunnerListenerLog;
import org.simulatest.environment.infra.EnvironmentExecutionException;
import org.simulatest.environment.mock.Environments.ColaboradorEnvironment;
import org.simulatest.environment.mock.Environments.DummyEnvironment;
import org.simulatest.environment.mock.Environments.EmpresaEnvironment;
import org.simulatest.environment.mock.Environments.ProjetoEnvironment;

public class EnvironmentRunnerTest {

	private EnvironmentTreeBuilder builder = new EnvironmentTreeBuilder();
	private EnvironmentFactory factory = new EnvironmentReflectionFactory();
	private EnvironmentRunnerListenerLog listenerLog = new EnvironmentRunnerListenerLog();

	@Test
	public void testListenerWithUniqueEnvironment() {
		builder.add(EnvironmentDefinition.bigBang());

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
		builder.add(EnvironmentDefinition.create(ColaboradorEnvironment.class));
		builder.add(EnvironmentDefinition.create(EmpresaEnvironment.class));
		builder.add(EnvironmentDefinition.create(ProjetoEnvironment.class));

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
	public void shouldThrowAnEnvironmentExecutionExceptionWhenSomethingWrongHappen() {
		builder.add(EnvironmentDefinition.create(DummyEnvironment.class));
		EnvironmentRunner runner = new EnvironmentRunner(factory, builder);
		runner.addListener(listenerLog);
		
		try {
			runner.run();
			fail("should throw an EnvironmentExecutionException");
		} catch (EnvironmentExecutionException exception) {
			String expectedMessage = "Error in execution of Environment: DummyEnvironment";
			assertEquals(expectedMessage, exception.getMessage());
		}
	}
	
}