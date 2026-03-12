package org.simulatest.environment.junit5.test.testdouble;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Simple tracker to verify environment execution order and test execution in integration tests.
 */
public class EnvironmentTracker {

	private static final List<String> events = new ArrayList<>();

	public static void record(String event) {
		events.add(event);
	}

	public static List<String> getEvents() {
		return Collections.unmodifiableList(events);
	}

	public static void clear() {
		events.clear();
	}

}
