package org.simulatest.environment.test;

import static org.junit.Assert.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.simulatest.environment.EnvironmentDefinition.create;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.environment.EnvironmentReflectionFactory;
import org.simulatest.environment.EnvironmentRunner;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.test.testdouble.Environments.ColaboradorEnvironment;
import org.simulatest.environment.test.testdouble.Environments.EmpresaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.PessoaFisicaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.ProjetoEnvironment;
import org.simulatest.environment.test.testdouble.Environments.Root;
import org.simulatest.environment.test.testdouble.ThrowingEnvironmentFactory;
import org.simulatest.environment.tree.EnvironmentTreeBuilder;
import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * How the runner drives the levels of the Insistence Layer while it walks
 * the tree: BigBang -> Root -> Pessoa, which has two branches,
 * PessoaFisica -> Colaborador and PessoaJuridica -> Empresa -> Projeto.
 */
public class EnvironmentRunnerLevelsTest {

	private static final int ENVIRONMENTS_IN_THE_TREE = 8;

	// PessoaFisica is the only one.
	private static final int ENVIRONMENTS_WITH_A_SIBLING_AFTER = 1;

	private EnvironmentTreeBuilder builder;
	private InsistenceLayer insistenceLayer;

	@Before
	public void setup() {
		builder = new EnvironmentTreeBuilder();
		builder.add(create(ColaboradorEnvironment.class));
		builder.add(create(EmpresaEnvironment.class));
		builder.add(create(ProjetoEnvironment.class));
		insistenceLayer = mock(InsistenceLayer.class);
	}

	@Test
	public void everyEnvironmentShouldOpenOneLevel() {
		runnerWith(new EnvironmentReflectionFactory()).run();

		verify(insistenceLayer, times(ENVIRONMENTS_IN_THE_TREE)).increaseLevel();
	}

	@Test
	public void anEnvironmentWithASiblingAfterItShouldCloseItsLevelAndResetTheParentInOneStep() {
		runnerWith(new EnvironmentReflectionFactory()).run();

		verify(insistenceLayer, times(ENVIRONMENTS_WITH_A_SIBLING_AFTER)).decreaseLevelAndResetOrCleanup();
		verify(insistenceLayer, never()).resetCurrentLevel();
	}

	@Test
	public void theLastSiblingShouldOnlyCloseItsLevel() {
		runnerWith(new EnvironmentReflectionFactory()).run();

		verify(insistenceLayer, times(ENVIRONMENTS_IN_THE_TREE - ENVIRONMENTS_WITH_A_SIBLING_AFTER))
				.decreaseLevelOrCleanup();
	}

	@Test
	public void aFailedEnvironmentShouldOpenNoLevel() {
		EnvironmentRunner runner = runnerWith(new ThrowingEnvironmentFactory(Root.class));

		assertThrows(EnvironmentExecutionException.class, runner::run);

		// Only BigBang opens a level, and still closes it on the way out.
		verify(insistenceLayer, times(1)).increaseLevel();
		verify(insistenceLayer, times(1)).decreaseLevelOrCleanup();
	}

	@Test
	public void aFailedEnvironmentWithASiblingAfterItShouldOnlyResetTheLevelOfTheParent() {
		EnvironmentRunner runner = runnerWith(new ThrowingEnvironmentFactory(PessoaFisicaEnvironment.class));
		runner.onEnvironmentFailure((definition, failure) -> { });

		runner.run();

		verify(insistenceLayer, times(1)).resetCurrentLevel();
		verify(insistenceLayer, never()).decreaseLevelAndResetOrCleanup();
	}

	@Test
	public void anEnvironmentWhoseLevelCannotOpenShouldNotCloseTheLevelOfItsParent() {
		doNothing().doThrow(new IllegalStateException("savepoint failed")).when(insistenceLayer).increaseLevel();
		EnvironmentRunner runner = runnerWith(new EnvironmentReflectionFactory());

		assertThrows(IllegalStateException.class, runner::run);

		// BigBang opened a level and Root could not: only one level is closed.
		verify(insistenceLayer, times(1)).decreaseLevelOrCleanup();
		verify(insistenceLayer, never()).decreaseLevelAndResetOrCleanup();
	}

	private EnvironmentRunner runnerWith(EnvironmentFactory factory) {
		return new EnvironmentRunner(factory, builder.getTree(), insistenceLayer);
	}

}
