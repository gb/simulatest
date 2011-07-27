package objectiveLabs.environmentTestHarness.environment;

import objectiveLabs.environmentTestHarness.spring.SpringContext;

import org.springframework.context.support.ClassPathXmlApplicationContext;

public class EnvironmentSpringFactory implements EnvironmentFactory {

	@Override
	public Environment create(EnvironmentDefinition definition) {
		if (SpringContext.getApplicationContext() == null) initializeSpring();
		return SpringContext.getApplicationContext().getBean(definition.getEnvironmentClass());
	}

	private void initializeSpring() {
		System.out.println("initializeSpring()");
		new ClassPathXmlApplicationContext("applicationContext.xml");
	}
	
}