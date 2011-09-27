package org.simulatest.environment.infra;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.apache.log4j.Logger;
import org.simulatest.environment.environment.Environment;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;

public class EnvironmentScanner extends ClassPathScanningCandidateComponentProvider {
	
	private final Logger logger = Logger.getLogger(EnvironmentScanner.class);
	private final Set<Class<?>> environments = new HashSet<Class<?>>();

	public EnvironmentScanner() {
		this("");
	}

	public EnvironmentScanner(String basePackage) {
		super(false);
		addIncludeFilter(new AssignableTypeFilter(Environment.class));
		scan(basePackage);
	}

	private void scan(String basePackage) {
		for (BeanDefinition candidate : findCandidateComponents(basePackage)) add(candidate);
	}

	private void add(BeanDefinition candidate) {
		try {
			environments.add(Class.forName(candidate.getBeanClassName()));
		} catch (ClassNotFoundException exception) {
			logger.error(exception);
		}
	}

	public Collection<Class<?>> getEnvironmentList() {
		return Collections.unmodifiableSet(environments);
	}

}