package objectivelabs.environmentTestHarness.spike;

import objectiveLabs.environmentTestHarness.junit.EnvironmentJUnitSuite;
import objectivelabs.environmentTestHarness.mock.Suites.EmpresaSuite;
import objectivelabs.environmentTestHarness.mock.Suites.PessoaSuite;
import objectivelabs.environmentTestHarness.test.NodeTest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(EnvironmentJUnitSuite.class)
@SuiteClasses({
	PessoaSuite.class, 
	EmpresaSuite.class,
	NodeTest.class})
public class IntegracaoSuite { 
	
}