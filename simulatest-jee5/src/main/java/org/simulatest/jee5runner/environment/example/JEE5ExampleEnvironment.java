package org.simulatest.jee5runner.environment.example;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.simulatest.jee5runner.test.mock.DatabaseMock;

@Stateless
public class JEE5ExampleEnvironment implements JEE5ExampleEnvironmentLocal {

	@EJB
	LanguageTeacher languageTeacher;
	
	@Override
	public void run() {
		DatabaseMock.addMessage(languageTeacher.sayHello());
	}
	
}