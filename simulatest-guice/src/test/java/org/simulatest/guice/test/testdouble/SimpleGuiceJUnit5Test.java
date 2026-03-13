package org.simulatest.guice.test.testdouble;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.guice.SimulatestGuiceConfig;
import org.simulatest.guice.test.TestModule;
import org.simulatest.guice.test.testdouble.environment.GuiceChildExampleEnvironment;
import org.simulatest.guice.test.testdouble.mock.DatabaseMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@SimulatestGuiceConfig(TestModule.class)
@UseEnvironment(GuiceChildExampleEnvironment.class)
public class SimpleGuiceJUnit5Test {

	@Inject
	private LanguageTeacher languageTeacher;

	@Inject
	private DatabaseMock databaseMock;

	@Test
	void guiceDIShouldWork() {
		assertEquals("Hello", languageTeacher.sayHello());
	}

	@Test
	void environmentsShouldHaveRun() {
		assertEquals(2, databaseMock.getMessages().size());
		assertEquals("Hello", databaseMock.getMessages().get(0));
		assertEquals("Hello by child", databaseMock.getMessages().get(1));
	}

}
