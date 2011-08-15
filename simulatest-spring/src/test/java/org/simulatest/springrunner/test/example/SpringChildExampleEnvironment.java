package org.simulatest.springrunner.test.example;

import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.environment.Environment;
import org.simulatest.springrunner.test.example.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@EnvironmentParent(value = SpringExampleEnvironment.class)
public class SpringChildExampleEnvironment implements Environment {

	@Autowired
	LanguageTeacher languageTeacher;
	
	@Autowired
	DatabaseMock databaseMock;
	
	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello() + " by child");
	}

}