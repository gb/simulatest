package org.simulatest.springrunner.test;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.springrunner.junit.SpringTestHarness;
import org.simulatest.springrunner.test.example.LanguageTeacher;
import org.simulatest.springrunner.test.example.SpringChildExampleEnvironment;
import org.simulatest.springrunner.test.example.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;

@UseEnvironment(SpringChildExampleEnvironment.class)
public class SimpleSpringTest extends SpringTestHarness {
	
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
		assertEquals(databaseMock.getMessages().get(0), "Hello");
		assertEquals(databaseMock.getMessages().get(1), "Hello by child");
	}

}