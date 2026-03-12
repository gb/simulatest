package org.simulatest.springrunner.junit5.test.testdouble;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.springrunner.junit5.test.testdouble.environment.SpringChildExampleEnvironment;
import org.simulatest.springrunner.junit5.test.testdouble.mock.DatabaseMock;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;
import org.springframework.beans.factory.annotation.Autowired;

@SimulatestSpringConfig(org.simulatest.springrunner.junit5.test.TestConfig.class)
@UseEnvironment(SpringChildExampleEnvironment.class)
public class SimpleSpringJUnit5Test {

	@Autowired
	private LanguageTeacher languageTeacher;

	@Autowired
	private DatabaseMock databaseMock;

	@Test
	public void springDIShouldWork() {
		Assertions.assertEquals("Hello", languageTeacher.sayHello());
	}

	@Test
	public void environmentsShouldHaveRun() {
		Assertions.assertEquals(2, databaseMock.getMessages().size());
		Assertions.assertEquals("Hello", databaseMock.getMessages().get(0));
		Assertions.assertEquals("Hello by child", databaseMock.getMessages().get(1));
	}

}
