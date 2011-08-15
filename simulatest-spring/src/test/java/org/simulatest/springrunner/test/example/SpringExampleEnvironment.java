package org.simulatest.springrunner.test.example;

import org.simulatest.environment.environment.Environment;
import org.simulatest.springrunner.test.example.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringExampleEnvironment implements Environment {

	@Autowired
	LanguageTeacher languageTeacher;
	
	@Autowired
	DatabaseMock databaseMock;
	
	@Override
	public void run() {
		databaseMock.addMessage(languageTeacher.sayHello());
	}

}