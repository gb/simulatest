package org.simulatest.springrunner.test.example;

import org.simulatest.environment.environment.Environment;
import org.simulatest.springrunner.test.mock.DatabaseMock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SpringExampleEnvironment implements Environment {

	@Autowired
	LanguageTeacher languageTeacher;
	
	@Override
	public void run() {
		DatabaseMock.addMessage(languageTeacher.sayHello());
	}

}