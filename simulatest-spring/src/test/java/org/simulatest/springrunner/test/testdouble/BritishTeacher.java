package org.simulatest.springrunner.test.testdouble;

import org.springframework.stereotype.Component;

@Component
public class BritishTeacher implements LanguageTeacher {

	@Override
	public String sayHello() {
		return "Hello";
	}

}
