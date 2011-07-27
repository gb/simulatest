package objectivelabs.environmentTestHarness.mock;

import objectiveLabs.environmentTestHarness.annotation.UseEnvironment;
import objectiveLabs.environmentTestHarness.junit.EnvironmentJUnitRunner;
import objectivelabs.environmentTestHarness.mock.Environments.EmpresaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaFisicaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaJuridicaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.ProjetoEnvironment;

import org.junit.Test;
import org.junit.runner.RunWith;

public class Tests {
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(PessoaFisicaEnvironment.class)
	public static class PessoaFisicaTest {
		@Test public void pessoaFisicaTest1() {};
		@Test public void pessoaFisicaTest2() {};
	}
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(PessoaFisicaEnvironment.class)
	public static class UsuarioTest {
		@Test public void usuarioTest() {};
	}
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(PessoaJuridicaEnvironment.class)
	public static class PessoaJuridicaTest {
		@Test public void pessoaJuridicaTest1() {};
		@Test public void pessoaJuridicaTest2() {};
		@Test public void pessoaJuridicaTest3() {};
		@Test public void pessoaJuridicaTest4() {};
	}
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(EmpresaEnvironment.class)
	public static class EmpresaTest {
		@Test public void empresaTest1() {};
		@Test public void empresaTest2() {};
	}
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(EmpresaEnvironment.class)
	public static class FuncionarioTest {
		@Test public void funcionarioTest1() {};
		@Test public void funcionarioTest2() {};
	}

	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(EmpresaEnvironment.class)
	public static class RelatorioLancamentosTest {
		@Test public void relatorioLancamentosTest1() {};
		@Test public void relatorioLancamentosTest2() {};
		@Test public void relatorioLancamentosTest3() {};
	}
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(EmpresaEnvironment.class)
	public static class RelatorioReceitaTest {
		@Test public void relatorioReceitaTest() {};
	}
	
	@RunWith(EnvironmentJUnitRunner.class)
	@UseEnvironment(ProjetoEnvironment.class)
	public static class RelatorioProjetosTest {
		@Test public void relatorioProjetosTest1() {};
		@Test public void relatorioProjetosTest2() {};
		@Test public void relatorioProjetosTest3() {};
	}
}
