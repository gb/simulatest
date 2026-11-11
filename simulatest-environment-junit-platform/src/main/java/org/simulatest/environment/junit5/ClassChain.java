package org.simulatest.environment.junit5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.simulatest.environment.annotation.UseEnvironment;

/**
 * A class together with the classes around it, from the outermost one to the
 * class itself.
 *
 * <p>The chain answers which {@link UseEnvironment @UseEnvironment} class owns
 * a test: the nearest annotated class, starting at the innermost.</p>
 *
 * <p><b>Thread-safety:</b> immutable; safe to share.</p>
 */
final class ClassChain {

	private final List<Class<?>> outermostFirst;

	private ClassChain(List<Class<?>> outermostFirst) {
		this.outermostFirst = List.copyOf(outermostFirst);
	}

	/** The chain a class has in the source code: its enclosing classes, then the class. */
	static ClassChain enclosing(Class<?> innermost) {
		List<Class<?>> chain = new ArrayList<>();
		for (Class<?> current = innermost; current != null; current = current.getEnclosingClass()) chain.add(current);
		Collections.reverse(chain);
		return new ClassChain(chain);
	}

	/**
	 * The chain a nested class is run in. It is not the chain in the source
	 * code when the nested class is inherited from a base class.
	 */
	static ClassChain of(List<Class<?>> enclosingClasses, Class<?> nestedClass) {
		List<Class<?>> chain = new ArrayList<>(enclosingClasses);
		chain.add(nestedClass);
		return new ClassChain(chain);
	}

	Class<?> innermost() {
		return outermostFirst.get(outermostFirst.size() - 1);
	}

	Optional<Class<?>> nearestWithEnvironment() {
		for (int index = outermostFirst.size() - 1; index >= 0; index--) {
			Class<?> candidate = outermostFirst.get(index);
			if (hasEnvironment(candidate)) return Optional.of(candidate);
		}
		return Optional.empty();
	}

	static boolean hasEnvironment(Class<?> candidate) {
		return candidate.isAnnotationPresent(UseEnvironment.class);
	}

}
