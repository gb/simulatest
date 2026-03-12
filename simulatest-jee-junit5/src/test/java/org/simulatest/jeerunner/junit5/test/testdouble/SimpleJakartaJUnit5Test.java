package org.simulatest.jeerunner.junit5.test.testdouble;

import jakarta.inject.Inject;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.jeerunner.junit.SimulatestJakartaRunner;
import org.simulatest.jeerunner.junit5.test.testdouble.environment.JakartaChildExampleEnvironment;
import org.simulatest.jeerunner.junit5.test.testdouble.mock.DatabaseMock;

import static org.junit.jupiter.api.Assertions.assertEquals;

@UseEnvironment(JakartaChildExampleEnvironment.class)
@RunWith(SimulatestJakartaRunner.class)
public class SimpleJakartaJUnit5Test {

	@Inject
	DatabaseMock databaseMock;

	@Inject
	BritishTeacher teacher;

	@Test
	public void environmentsShouldHaveRun() {
		assertEquals(2, databaseMock.getMessages().size());
	}

	@Test
	public void cdiLookupShouldWork() {
		assertEquals("Hello", teacher.sayHello());
	}

}
