package org.simulatest.springrunner.junit5.test.testdouble.environment;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.environment.Environment;
import org.simulatest.springrunner.junit5.test.testdouble.LanguageTeacher;
import org.simulatest.springrunner.junit5.test.testdouble.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnvironmentParent(SpringExampleEnvironment.class)
public class SpringChildExampleEnvironment implements Environment {

	@Autowired
	private LanguageTeacher languageTeacher;

	@Autowired
	private DatabaseMock databaseMock;

	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello() + " by child");
	}

}
