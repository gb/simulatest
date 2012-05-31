package org.simulatest.springrunner.spring;

import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.stereotype.Component;

@Component
public class SpringContext implements ApplicationContextAware {
	
	private static ApplicationContext context;
	private static ClassPathXmlApplicationContext classPath;

	public void setApplicationContext(ApplicationContext context) throws BeansException {
		SpringContext.context = context;
	}
	
	public static <T> T getBean(Class<T> clazz) {
		return getApplicationContext().getBean(clazz);
	}

	public static ApplicationContext getApplicationContext() {
		return context;
	}
	
	public static void initializeSpring() {
		classPath = new ClassPathXmlApplicationContext("simulatest-applicationContext.xml");
	}
	
	public static void destroy() {
		classPath.close();
	}
	
}