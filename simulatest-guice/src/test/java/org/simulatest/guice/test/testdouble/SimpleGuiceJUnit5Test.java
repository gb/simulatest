package org.simulatest.guice.test.testdouble;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.guice.SimulatestGuiceConfig;
import org.simulatest.guice.test.TestModule;
import org.simulatest.guice.test.testdouble.environment.GuiceChildExampleEnvironment;
import org.simulatest.guice.test.testdouble.mock.DatabaseMock;

@SimulatestGuiceConfig(TestModule.class)
@UseEnvironment(GuiceChildExampleEnvironment.class)
public class SimpleGuiceJUnit5Test {

	@Inject
	private LanguageTeacher languageTeacher;

	@Inject
	private DatabaseMock databaseMock;

	@Test
	void guiceDIShouldWork() {
		Assertions.assertEquals("Hello", languageTeacher.sayHello());
	}

	@Test
	void environmentsShouldHaveRun() {
		Assertions.assertEquals(2, databaseMock.getMessages().size());
		Assertions.assertEquals("Hello", databaseMock.getMessages().get(0));
		Assertions.assertEquals("Hello by child", databaseMock.getMessages().get(1));
	}

}
