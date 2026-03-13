package org.simulatest.jeerunner.test;

import static org.junit.Assert.assertEquals;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.insistencelayer.datasource.InsistenceLayerDataSource;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.jeerunner.cdi.CdiContext;
import org.simulatest.jeerunner.test.example.BritishTeacher;
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

	@Test
	public void testEnvironments() {
		DatabaseMock databaseMock = CdiContext.getBean(DatabaseMock.class);
		assertEquals(2, databaseMock.getMessages().size());
	}

	@Test
	public void testCdiLookup() {
		LanguageTeacher teacher = CdiContext.getBean(BritishTeacher.class);
		assertEquals("Hello", teacher.sayHello());
	}

}
