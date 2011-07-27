package objectivelabs.environmentTestHarness.mock;

import objectiveLabs.environmentTestHarness.junit.EnvironmentJUnitSuite;
import objectivelabs.environmentTestHarness.mock.Tests.EmpresaTest;
import objectivelabs.environmentTestHarness.mock.Tests.FuncionarioTest;
import objectivelabs.environmentTestHarness.mock.Tests.PessoaFisicaTest;
import objectivelabs.environmentTestHarness.mock.Tests.PessoaJuridicaTest;
import objectivelabs.environmentTestHarness.mock.Tests.RelatorioLancamentosTest;
import objectivelabs.environmentTestHarness.mock.Tests.RelatorioProjetosTest;
import objectivelabs.environmentTestHarness.mock.Tests.RelatorioReceitaTest;
import objectivelabs.environmentTestHarness.mock.Tests.UsuarioTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

public class Suites {

	@RunWith(EnvironmentJUnitSuite.class)
	@SuiteClasses({
		PessoaSuite.class, 
		EmpresaSuite.class})
	public static class IntegracaoSuite { }
	
	@RunWith(EnvironmentJUnitSuite.class)
	@SuiteClasses({
		PessoaFisicaTest.class,
		PessoaJuridicaTest.class,
		UsuarioTest.class})
	public static class PessoaSuite { }
	
	@RunWith(EnvironmentJUnitSuite.class)
	@SuiteClasses({
		RelatorioSuite.class,
		EmpresaTest.class,
		FuncionarioTest.class})
	public static class EmpresaSuite { }
	
	@RunWith(EnvironmentJUnitSuite.class)
	@SuiteClasses({
		RelatorioLancamentosTest.class,
		RelatorioReceitaTest.class,
		RelatorioProjetosTest.class
	})
	public static class RelatorioSuite { }
}
