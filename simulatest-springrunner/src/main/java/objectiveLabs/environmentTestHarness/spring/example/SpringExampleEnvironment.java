package objectiveLabs.environmentTestHarness.spring.example;

import objectiveLabs.environmentTestHarness.environment.Environment;
import objectiveLabs.environmentTestHarness.spring.mock.DatabaseMock;

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