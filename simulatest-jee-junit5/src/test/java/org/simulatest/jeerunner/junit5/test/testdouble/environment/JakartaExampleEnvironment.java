package org.simulatest.jeerunner.junit5.test.testdouble.environment;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.simulatest.environment.environment.Environment;
import org.simulatest.jeerunner.junit5.test.testdouble.LanguageTeacher;
import org.simulatest.jeerunner.junit5.test.testdouble.mock.DatabaseMock;

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
