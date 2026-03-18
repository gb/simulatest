package org.simulatest.guice.test.testdouble.environment;

import jakarta.inject.Inject;

import org.simulatest.environment.Environment;
import org.simulatest.guice.test.testdouble.LanguageTeacher;
import org.simulatest.guice.test.testdouble.mock.DatabaseMock;

public class GuiceExampleEnvironment implements Environment {

	@Inject
	private LanguageTeacher languageTeacher;

	@Inject
	private DatabaseMock databaseMock;

	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello());
	}

}
