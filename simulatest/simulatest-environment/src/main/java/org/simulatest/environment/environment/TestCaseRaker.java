package org.simulatest.environment.environment;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.runners.Suite.SuiteClasses;

import com.google.common.base.Preconditions;

public class TestCaseRaker {
	
	private Set<Class<?>> allTests = new HashSet<Class<?>>();
	
	public TestCaseRaker(Class<?> mainSuite) {
		digesterSuite(mainSuite);
	}
	
	public Set<Class<?>> getAllTests() {
		return Collections.unmodifiableSet(allTests);
	}
		
	private void digesterSuite(Class<?> suite) {
		Preconditions.checkArgument(isSuiteCase(suite), "TestCaseRaker demands a SuiteTest Class");
		for (Class<?> clazz : getSuiteClasses(suite)) addTest(clazz);
	}

	private void addTest(Class<?> clazz) {
		if (isSuiteCase(clazz)) digesterSuite(clazz);
		else allTests.add(clazz);
	}
	
	private boolean isSuiteCase(Class<?> clazz) {
		return clazz.getAnnotation(SuiteClasses.class) != null;
	}
	
	private Class<?>[] getSuiteClasses(Class<?> clazz) {
		return clazz.getAnnotation(SuiteClasses.class).value();
	}

}