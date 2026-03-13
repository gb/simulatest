package org.simulatest.springrunner.test.testdouble.environment;

import org.simulatest.environment.environment.Environment;
import org.simulatest.springrunner.test.testdouble.LanguageTeacher;
import org.simulatest.springrunner.test.testdouble.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringExampleEnvironment implements Environment {

	@Autowired
	private LanguageTeacher languageTeacher;

	@Autowired
	private DatabaseMock databaseMock;

	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello());
	}

}
