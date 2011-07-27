package org.simulatest.springrunner.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext context;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContext.context = context;
	}
	
	public static Object getBean(String name) {
		return getApplicationContext().getBean(name);
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}
	
}