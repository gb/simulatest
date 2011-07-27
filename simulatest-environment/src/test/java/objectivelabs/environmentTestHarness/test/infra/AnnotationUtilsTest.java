package objectivelabs.environmentTestHarness.test.infra;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import objectiveLabs.environmentTestHarness.environment.BigBangEnvironment;
import objectiveLabs.environmentTestHarness.infra.AnnotationUtils;
import objectivelabs.environmentTestHarness.mock.Environments.EmpresaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaFisicaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.PessoaJuridicaEnvironment;
import objectivelabs.environmentTestHarness.mock.Environments.Root;
import objectivelabs.environmentTestHarness.mock.Tests.EmpresaTest;
import objectivelabs.environmentTestHarness.mock.Tests.PessoaFisicaTest;
import objectivelabs.environmentTestHarness.mock.Tests.UsuarioTest;

import org.junit.Test;

public class AnnotationUtilsTest {
	
	@Test
	public void extractEnvironmentTest() {
		assertEnvironmentEquals(BigBangEnvironment.class, Root.class);
		assertEnvironmentEquals(PessoaFisicaEnvironment.class, PessoaFisicaTest.class);
		assertEnvironmentEquals(PessoaFisicaEnvironment.class, UsuarioTest.class);
		assertEnvironmentEquals(EmpresaEnvironment.class, EmpresaTest.class);
		assertNull(AnnotationUtils.extractEnvironment(BigBangEnvironment.class));
	}
	
	@Test
	public void extractEnvironmentParentTest() {
		assertParentEnvirontmentEquals(Root.class, PessoaEnvironment.class);
		assertParentEnvirontmentEquals(PessoaEnvironment.class, PessoaFisicaEnvironment.class);
		assertParentEnvirontmentEquals(PessoaEnvironment.class, PessoaJuridicaEnvironment.class);
		assertParentEnvirontmentEquals(BigBangEnvironment.class, Root.class);
		assertNull(AnnotationUtils.extractEnvironmentParent(BigBangEnvironment.class));
	}
	
	private void assertEnvironmentEquals(Class<?> expectedEnvironment, Class<?> clazz) {
		assertEquals(expectedEnvironment, AnnotationUtils.extractEnvironment(clazz));
	}
	
	private void assertParentEnvirontmentEquals(Class<?> expectedEnvironment, Class<?> clazz) {
		assertEquals(expectedEnvironment, AnnotationUtils.extractEnvironmentParent(clazz));
	}
	
}