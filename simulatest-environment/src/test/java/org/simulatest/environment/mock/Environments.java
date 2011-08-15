package org.simulatest.environment.mock;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.environment.Environment;

public class Environments {
	
	public static class Root implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(Root.class)
	public static class PessoaEnvironment implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(PessoaEnvironment.class)
	public static class PessoaFisicaEnvironment implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(PessoaEnvironment.class)
	public static class PessoaJuridicaEnvironment implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(PessoaFisicaEnvironment.class)
	public static class ColaboradorEnvironment implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(PessoaJuridicaEnvironment.class)
	public static class EmpresaEnvironment implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(EmpresaEnvironment.class)
	public static class ProjetoEnvironment implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(ProjetoEnvironmentCiclico.class)
	public static class ProjetoEnvironmentCiclico implements Environment {
		public void run() {}
	}
	
	@EnvironmentParent(CyclicEnvironmentTwo.class)
	public static class CyclicEnvironmentOne implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentOne.class)
	public static class CyclicEnvironmentTwo implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentB.class)
	public static class CyclicEnvironmentA implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentC.class)
	public static class CyclicEnvironmentB implements Environment {
		public void run() { }
	}
	
	@EnvironmentParent(CyclicEnvironmentA.class)
	public static class CyclicEnvironmentC implements Environment {
		public void run() { }
	}
	
	public static class PrivateConstructorEnvironment implements Environment {
		private PrivateConstructorEnvironment() { }
		public void run() { }
	}
	
	public static class DummyEnvironment implements Environment {
		public void run() { @SuppressWarnings("unused") int number = 0 / 0; }
	}
	
}