package org.simulatest.environment.mock;


import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.mock.Tests.EmpresaTest;
import org.simulatest.environment.mock.Tests.FuncionarioTest;
import org.simulatest.environment.mock.Tests.PessoaFisicaTest;
import org.simulatest.environment.mock.Tests.PessoaJuridicaTest;
import org.simulatest.environment.mock.Tests.RelatorioLancamentosTest;
import org.simulatest.environment.mock.Tests.RelatorioProjetosTest;
import org.simulatest.environment.mock.Tests.RelatorioReceitaTest;
import org.simulatest.environment.mock.Tests.UsuarioTest;


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
