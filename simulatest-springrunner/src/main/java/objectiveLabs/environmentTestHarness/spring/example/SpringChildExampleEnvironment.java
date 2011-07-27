package objectiveLabs.environmentTestHarness.spring.example;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import objectiveLabs.environmentTestHarness.annotation.EnvironmentParent;
import objectiveLabs.environmentTestHarness.environment.Environment;
import objectiveLabs.environmentTestHarness.spring.mock.DatabaseMock;

@Component
@EnvironmentParent(value = SpringExampleEnvironment.class)
public class SpringChildExampleEnvironment implements Environment {

	@Autowired
	LanguageTeacher languageTeacher;
	
	@Override
	public void run() {
		DatabaseMock.addMessage(languageTeacher.sayHello() + " by child");
	}

}