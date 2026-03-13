package org.simulatest.jeerunner.test;

import static org.junit.Assert.assertEquals;

import jakarta.inject.Inject;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.jeerunner.test.example.JakartaChildExampleEnvironment;
import org.simulatest.jeerunner.test.example.LanguageTeacher;
import org.simulatest.jeerunner.test.example.mock.DatabaseMock;

@RunWith(EnvironmentJUnitRunner.class)
@UseEnvironment(JakartaChildExampleEnvironment.class)
public class SimpleJakartaTest {

	static {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:~/.h2/test");
		h2.setUser("sa");
		InsistenceLayerDataSource.configure(h2);
	}

	@Inject
	private DatabaseMock databaseMock;

	@Inject
	private LanguageTeacher languageTeacher;

	@Test
	public void testEnvironments() {
		assertEquals(2, databaseMock.getMessages().size());
	}

	@Test
	public void testCdiLookup() {
		assertEquals("Hello", languageTeacher.sayHello());
	}

}
