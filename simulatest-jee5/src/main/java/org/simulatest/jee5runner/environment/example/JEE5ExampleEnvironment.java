package org.simulatest.jee5runner.environment.example;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.simulatest.jee5runner.environment.example.mock.DatabaseMock;

@Stateless
public class JEE5ExampleEnvironment implements JEE5ExampleEnvironmentLocal {

	@EJB
	LanguageTeacher languageTeacher;
	
	@EJB
	private DatabaseMock databaseMock;
	
	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello());
	}
	
}