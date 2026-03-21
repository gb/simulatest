package org.simulatest.jeerunner.test.testdouble.environment;

import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.Environment;
import org.simulatest.jeerunner.test.testdouble.LanguageTeacher;
import org.simulatest.jeerunner.test.testdouble.mock.DatabaseMock;

@Dependent
@EnvironmentParent(JakartaExampleEnvironment.class)
public class JakartaChildExampleEnvironment implements Environment {

	@Inject
	private LanguageTeacher languageTeacher;

	@Inject
	private DatabaseMock databaseMock;

	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello() + " by child");
	}

}
