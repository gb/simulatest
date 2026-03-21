package org.simulatest.jeerunner.test.example;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BritishTeacher implements LanguageTeacher {

	@Override
	public String sayHello() {
		return "Hello";
	}

}
