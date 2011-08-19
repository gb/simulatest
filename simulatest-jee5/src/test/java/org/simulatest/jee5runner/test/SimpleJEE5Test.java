package org.simulatest.jee5runner.test;

import static junit.framework.Assert.assertEquals;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.jee5runner.environment.example.BritishTeacher;
import org.simulatest.jee5runner.environment.example.JEE5ChildExampleEnvironment;
import org.simulatest.jee5runner.environment.example.LanguageTeacher;
import org.simulatest.jee5runner.environment.example.mock.DatabaseMock;
import org.simulatest.jee5runner.environment.example.mock.DatabaseMockImpl;
import org.simulatest.jee5runner.jee5.JEE5Context;
import org.simulatest.jee5runner.junit.EnvironmentJEE5Runner;

@RunWith(EnvironmentJEE5Runner.class)
@UseEnvironment(JEE5ChildExampleEnvironment.class)
public class SimpleJEE5Test {
	
	DatabaseMock databaseMock = (DatabaseMock) JEE5Context.lookup(DatabaseMockImpl.class);

	@Test
	public void testEnvironments() {
		assertEquals(2, databaseMock.getMessages().size());
		assertEquals(databaseMock.getMessages().get(0), "Hello");
		assertEquals(databaseMock.getMessages().get(1), "Hello by child");
	}
	
	@Test
	public void testEJBLookup() {
		LanguageTeacher languageTeacher = (LanguageTeacher) JEE5Context.lookup(BritishTeacher.class);
		assertEquals("Hello", languageTeacher.sayHello());
	}
	
}