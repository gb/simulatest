package org.simulatest.jee5runner.environment.example;

import javax.ejb.Stateless;

@Stateless
public class BritishTeacher implements LanguageTeacher {

	@Override
	public String sayHello() {
		return "Hello";
	}

}
