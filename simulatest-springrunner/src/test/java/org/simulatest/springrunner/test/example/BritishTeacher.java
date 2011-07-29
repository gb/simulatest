package org.simulatest.springrunner.test.example;

import org.springframework.stereotype.Component;

@Component
public class BritishTeacher implements LanguageTeacher {

	@Override
	public String sayHello() {
		return "Hello";
	}

}