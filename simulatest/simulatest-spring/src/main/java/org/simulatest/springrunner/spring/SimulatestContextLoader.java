package org.simulatest.springrunner.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.test.context.ContextLoader;

public class SimulatestContextLoader implements ContextLoader {

	@Override
	public String[] processLocations(Class<?> clazz, String... locations) {
		return locations;
	}

	@Override
	public ApplicationContext loadContext(String... locations) throws Exception {
		return SpringContext.getApplicationContext();
	}

}