package org.simulatest.environment.test.testdouble;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.Environment;

public class Environments {
	
	public static class Root implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(Root.class)
	public static class PessoaEnvironment implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(PessoaEnvironment.class)
	public static class PessoaFisicaEnvironment implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(PessoaEnvironment.class)
	public static class PessoaJuridicaEnvironment implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(PessoaFisicaEnvironment.class)
	public static class ColaboradorEnvironment implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(PessoaJuridicaEnvironment.class)
	public static class EmpresaEnvironment implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(EmpresaEnvironment.class)
	public static class ProjetoEnvironment implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(ProjetoEnvironmentCiclico.class)
	public static class ProjetoEnvironmentCiclico implements Environment {
		@Override public void run() {}
	}
	
	@EnvironmentParent(CyclicEnvironmentTwo.class)
	public static class CyclicEnvironmentOne implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentOne.class)
	public static class CyclicEnvironmentTwo implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentB.class)
	public static class CyclicEnvironmentA implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentC.class)
	public static class CyclicEnvironmentB implements Environment {
		@Override public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentA.class)
	public static class CyclicEnvironmentC implements Environment {
		@Override public void run() { }
	}
	
	public static class NoDefaultConstructorEnvironment implements Environment {
		public NoDefaultConstructorEnvironment(String required) { }
		@Override public void run() { }
	}
	
	public static class DummyEnvironment implements Environment {
		@Override public void run() { @SuppressWarnings("unused") int number = 0 / 0; }
	}
	
}