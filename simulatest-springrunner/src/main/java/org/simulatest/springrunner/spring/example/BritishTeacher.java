package org.simulatest.springrunner.spring.example;

import org.springframework.stereotype.Component;

@Component
public class BritishTeacher implements LanguageTeacher {

	@Override
	public String sayHello() {
		return "Hello";
	}

}