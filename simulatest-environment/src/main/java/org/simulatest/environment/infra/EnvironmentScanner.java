package org.simulatest.environment.infra;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.simulatest.environment.environment.Environment;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.core.type.filter.AssignableTypeFilter;
import org.springframework.util.ClassUtils;

public class EnvironmentScanner extends ClassPathScanningCandidateComponentProvider {

	private final Set<Class<? extends Environment>> environments = new HashSet<Class<? extends Environment>>();

	public EnvironmentScanner() {
		this("");
	}

	public EnvironmentScanner(String basePackage) {
		super(true);
		addIncludeFilter(new AssignableTypeFilter(Environment.class));
		scan(basePackage);
	}

	private void scan(String basePackage) {
		for (BeanDefinition candidate : findCandidateComponents(basePackage)) add(candidate);
	}

	private void add(BeanDefinition candidate) {
		Class<?> clazz = ClassUtils.resolveClassName(candidate.getBeanClassName(), ClassUtils.getDefaultClassLoader());
		if (!Environment.class.isAssignableFrom(clazz)) return;
		environments.add((Class<? extends Environment>) clazz);
	}

	public Collection<Class<? extends Environment>> getEnvironmentList() {
		return Collections.unmodifiableSet(environments);
	}

}