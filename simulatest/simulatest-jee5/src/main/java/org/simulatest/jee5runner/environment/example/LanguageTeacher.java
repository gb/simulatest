package org.simulatest.jee5runner.environment.example;

import javax.ejb.Local;

@Local
public interface LanguageTeacher {
	
	String sayHello();

}
