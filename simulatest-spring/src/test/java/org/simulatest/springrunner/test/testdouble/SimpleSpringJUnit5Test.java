package org.simulatest.springrunner.test.testdouble;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;
import org.simulatest.springrunner.test.testdouble.environment.SpringChildExampleEnvironment;
import org.simulatest.springrunner.test.testdouble.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;

@SimulatestSpringConfig(org.simulatest.springrunner.test.TestDoubleConfig.class)
@UseEnvironment(SpringChildExampleEnvironment.class)
class SimpleSpringJUnit5Test {

	@Autowired
	private LanguageTeacher languageTeacher;

	@Autowired
	private DatabaseMock databaseMock;

	@Test
	void springDIShouldWork() {
		Assertions.assertEquals("Hello", languageTeacher.sayHello());
	}

	@Test
	void environmentsShouldHaveRun() {
		Assertions.assertEquals(2, databaseMock.getMessages().size());
		Assertions.assertEquals("Hello", databaseMock.getMessages().get(0));
		Assertions.assertEquals("Hello by child", databaseMock.getMessages().get(1));
	}

}
