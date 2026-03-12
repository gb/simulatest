package org.simulatest.environment.environment;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import io.github.classgraph.ClassGraph;
import io.github.classgraph.ScanResult;

public class EnvironmentScanner {

	private final Set<Class<?>> environments = new HashSet<>();

	public EnvironmentScanner() {
		this("");
	}

	public EnvironmentScanner(String basePackage) {
		scan(basePackage);
	}

	private void scan(String basePackage) {
		ClassGraph classGraph = new ClassGraph().enableClassInfo();
		if (!basePackage.isEmpty()) {
			classGraph.acceptPackages(basePackage);
		}

		try (ScanResult result = classGraph.scan()) {
			environments.addAll(result.getClassesImplementing(Environment.class)
					.loadClasses());
		}
	}

	public Collection<Class<?>> getEnvironmentList() {
		return Collections.unmodifiableSet(environments);
	}

}
