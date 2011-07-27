package org.simulatest.environment.spike;


import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.environment.junit.EnvironmentJUnitSuite;
import org.simulatest.environment.mock.Suites.EmpresaSuite;
import org.simulatest.environment.mock.Suites.PessoaSuite;
import org.simulatest.environment.test.NodeTest;


@RunWith(EnvironmentJUnitSuite.class)
@SuiteClasses({
	PessoaSuite.class, 
	EmpresaSuite.class,
	NodeTest.class})
public class IntegracaoSuite { 
	
}