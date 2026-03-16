package org.simulatest.springrunner.test;

import static org.junit.Assert.assertEquals;

import org.h2.jdbcx.JdbcDataSource;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;
import org.simulatest.springrunner.test.example.LanguageTeacher;
import org.simulatest.springrunner.test.example.SpringChildExampleEnvironment;
import org.simulatest.springrunner.test.example.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;

@RunWith(EnvironmentJUnitRunner.class)
@SimulatestSpringConfig(TestConfig.class)
@UseEnvironment(SpringChildExampleEnvironment.class)
public class SimpleSpringTest {

	static {
		JdbcDataSource h2 = new JdbcDataSource();
		h2.setURL("jdbc:h2:mem:springtest;DB_CLOSE_DELAY=-1");
		h2.setUser("sa");
		InsistenceLayerFactory.configure(h2);
	}

	@Autowired
	private LanguageTeacher languageTeacher;

	@Autowired
	private DatabaseMock databaseMock;

	@Test
	public void simpleSpringDITest() {
		assertEquals("Hello", languageTeacher.sayHello());
	}

	@Test
	public void environmentsTest() {
		assertEquals(2, databaseMock.getMessages().size());
		assertEquals("Hello", databaseMock.getMessages().get(0));
		assertEquals("Hello by child", databaseMock.getMessages().get(1));
	}

}
