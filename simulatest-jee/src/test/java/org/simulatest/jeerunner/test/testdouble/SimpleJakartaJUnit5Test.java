package org.simulatest.jeerunner.test.testdouble;

import static org.junit.jupiter.api.Assertions.assertEquals;

import jakarta.inject.Inject;

import org.junit.jupiter.api.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.jeerunner.test.testdouble.environment.JakartaChildExampleEnvironment;
import org.simulatest.jeerunner.test.testdouble.mock.DatabaseMock;

@UseEnvironment(JakartaChildExampleEnvironment.class)
class SimpleJakartaJUnit5Test {

	@Inject
	DatabaseMock databaseMock;

	@Inject
	BritishTeacher teacher;

	@Test
	void environmentsShouldHaveRun() {
		assertEquals(2, databaseMock.getMessages().size());
	}

	@Test
	void cdiLookupShouldWork() {
		assertEquals("Hello", teacher.sayHello());
	}

}
