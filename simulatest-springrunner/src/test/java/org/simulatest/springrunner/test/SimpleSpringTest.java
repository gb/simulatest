package org.simulatest.springrunner.test;

import static junit.framework.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.springrunner.junit.EnvironmentSpringRunner;
import org.simulatest.springrunner.test.example.LanguageTeacher;
import org.simulatest.springrunner.test.example.SpringChildExampleEnvironment;
import org.simulatest.springrunner.test.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;

@RunWith(EnvironmentSpringRunner.class)
@UseEnvironment(SpringChildExampleEnvironment.class)
@ContextConfiguration(locations={"/applicationContext.xml"})
public class SimpleSpringTest {
	
	@Autowired
	LanguageTeacher languageTeacher;
	
	@Test
	public void simpleSpringDITest() {
		assertEquals("Hello", languageTeacher.sayHello());
	}
	
	@Test
	public void environmentsTest() {
		assertEquals(2, DatabaseMock.getMessages().size());
		assertEquals(DatabaseMock.getMessages().get(0), "Hello");
		assertEquals(DatabaseMock.getMessages().get(1), "Hello by child");
		
		DatabaseMock.reseta();
		
		assertEquals(0, DatabaseMock.getMessages().size());
	}

}