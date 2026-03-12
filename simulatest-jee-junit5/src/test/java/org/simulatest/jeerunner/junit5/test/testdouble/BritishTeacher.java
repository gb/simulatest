package org.simulatest.jeerunner.junit5.test.testdouble;

import jakarta.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class BritishTeacher implements LanguageTeacher {

	@Override
	public String sayHello() {
		return "Hello";
	}

}
