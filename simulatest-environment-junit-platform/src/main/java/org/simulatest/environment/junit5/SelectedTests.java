package org.simulatest.environment.junit5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.platform.engine.DiscoverySelector;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * The {@link UseEnvironment @UseEnvironment} classes a discovery request asks
 * for, and which part of each one.
 *
 * <p>A class is selected either as a whole or through parts: the selectors
 * that name one of its methods, nested classes or single tests. Jupiter gets
 * the parts as they are, so it runs what was asked for and nothing else.
 * Once a class is selected as a whole its parts no longer matter.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe; built and read during one
 * discovery.</p>
 */
final class SelectedTests {

	// An empty list means the whole class.
	private final Map<Class<?>, List<DiscoverySelector>> partsByClass = new LinkedHashMap<>();

	void addWholeClass(Class<?> testClass) {
		partsByClass.put(testClass, new ArrayList<>());
	}

	void addPart(Class<?> testClass, DiscoverySelector part) {
		if (isWholeClass(testClass)) return;
		partsByClass.computeIfAbsent(testClass, key -> new ArrayList<>()).add(part);
	}

	Set<Class<?>> classes() {
		return Collections.unmodifiableSet(partsByClass.keySet());
	}

	/**
	 * Returns the selectors that narrow the run of {@code testClass}, or an
	 * empty list when the whole class is selected.
	 */
	List<DiscoverySelector> partsOf(Class<?> testClass) {
		return List.copyOf(partsByClass.getOrDefault(testClass, List.of()));
	}

	boolean isEmpty() {
		return partsByClass.isEmpty();
	}

	private boolean isWholeClass(Class<?> testClass) {
		List<DiscoverySelector> parts = partsByClass.get(testClass);
		return parts != null && parts.isEmpty();
	}

}
