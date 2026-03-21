package org.simulatest.jeerunner.test.example;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.simulatest.environment.Environment;
import org.simulatest.jeerunner.test.example.mock.DatabaseMock;

@Dependent
public class JakartaExampleEnvironment implements Environment {

	@Inject
	private LanguageTeacher languageTeacher;

	@Inject
	private DatabaseMock databaseMock;

	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello());
	}

}
