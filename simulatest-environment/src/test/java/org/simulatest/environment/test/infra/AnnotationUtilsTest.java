package org.simulatest.environment.test.infra;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.test.testdouble.Environments.PessoaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.PessoaFisicaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.PessoaJuridicaEnvironment;
import org.simulatest.environment.test.testdouble.Environments.Root;

public class AnnotationUtilsTest {

	@Test
	public void extractEnvironmentParentTest() {
		assertEquals(Root.class, EnvironmentDefinition.create(PessoaEnvironment.class).getParentClass());
		assertEquals(PessoaEnvironment.class, EnvironmentDefinition.create(PessoaFisicaEnvironment.class).getParentClass());
		assertEquals(PessoaEnvironment.class, EnvironmentDefinition.create(PessoaJuridicaEnvironment.class).getParentClass());
		assertEquals(BigBangEnvironment.class, EnvironmentDefinition.create(Root.class).getParentClass());
	}

}
