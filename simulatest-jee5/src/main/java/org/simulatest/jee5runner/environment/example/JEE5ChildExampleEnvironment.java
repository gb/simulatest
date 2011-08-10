package org.simulatest.jee5runner.environment.example;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.jee5runner.environment.example.mock.DatabaseMock;

@Stateless
@EnvironmentParent(value = JEE5ExampleEnvironment.class)
public class JEE5ChildExampleEnvironment implements JEE5ChildExampleEnvironmentLocal {
	
	@EJB
	private LanguageTeacher languageTeacher;
	
	@Override
	public void run() {
		DatabaseMock.addMessage(languageTeacher.sayHello() + " by child");
	}

}