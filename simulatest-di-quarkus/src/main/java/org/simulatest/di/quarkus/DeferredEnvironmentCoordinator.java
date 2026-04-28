package org.simulatest.di.quarkus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * Suite-wide tracking for environments that run inside Quarkus's
 * {@code @QuarkusTest} lifecycle rather than in the engine's tree walk.
 *
 * <p>{@link DeferredEnvironmentLifecycle} turns the engine's tree-walk
 * hooks into no-ops. {@link PostArcEnvironmentRunner} then runs the
 * ancestry from inside the inner Jupiter session after Arc has booted.
 * Sibling test classes share environment ancestors, so this coordinator
 * tracks two sets — environments that have been claimed (first-time-seen)
 * and environments whose Insistence Layer level was actually pushed — so
 * that {@link DeferredEnvironmentLifecycle#onExit} only pops levels that
 * exist on the stack.
 *
 * <p>Suite-wide static state is acceptable because the Insistence Layer's
 * single shared connection enforces single-threaded execution upstream.
 *
 * <p>Package-private; consumers outside this module have no business with
 * deferred-environment plumbing.
 */
final class DeferredEnvironmentCoordinator {

	private static final Set<Class<? extends Environment>> claimed = new LinkedHashSet<>();
	private static final Set<Class<? extends Environment>> pushed = new LinkedHashSet<>();

	private DeferredEnvironmentCoordinator() {}

	/**
	 * Returns the environment ancestry for {@code testClass} root-first: the
	 * outermost ancestor is first, the class's leaf {@link UseEnvironment}
	 * last. Empty list if neither {@code testClass} nor any of its enclosing
	 * classes carries {@code @UseEnvironment}.
	 */
	static List<Class<? extends Environment>> ancestryOf(Class<?> testClass) {
		UseEnvironment use = resolveUseEnvironment(testClass);
		if (use == null) return List.of();
		return walkParentChain(use.value());
	}

	/**
	 * Walks the {@code @EnvironmentParent} chain starting at {@code leaf},
	 * returning the chain root-first. Throws {@link IllegalStateException}
	 * if the chain is cyclic.
	 */
	static List<Class<? extends Environment>> walkParentChain(Class<? extends Environment> leaf) {
		List<Class<? extends Environment>> leafFirst = new ArrayList<>();
		Set<Class<? extends Environment>> visited = new LinkedHashSet<>();
		for (Class<? extends Environment> current = leaf; current != null; current = parentOf(current)) {
			if (!visited.add(current)) {
				throw new IllegalStateException(
					"Cyclic @EnvironmentParent chain detected starting at " + leaf.getName()
					+ ": " + visited);
			}
			leafFirst.add(current);
		}
		Collections.reverse(leafFirst);
		return leafFirst;
	}

	static synchronized boolean claimNotYetRun(Class<? extends Environment> env) {
		return claimed.add(env);
	}

	static synchronized void recordPush(Class<? extends Environment> env) {
		pushed.add(env);
	}

	static synchronized boolean wasPushed(Class<? extends Environment> env) {
		return pushed.contains(env);
	}

	static synchronized void forget(Class<? extends Environment> env) {
		claimed.remove(env);
		pushed.remove(env);
	}

	static synchronized void reset() {
		claimed.clear();
		pushed.clear();
	}

	private static UseEnvironment resolveUseEnvironment(Class<?> testClass) {
		return resolveUseEnvironmentClass(testClass)
				.map(c -> c.getAnnotation(UseEnvironment.class))
				.orElse(null);
	}

	// Walks the enclosing class chain so @Nested inner classes inherit the
	// outer class's environment.
	private static Optional<Class<?>> resolveUseEnvironmentClass(Class<?> clazz) {
		for (Class<?> current = clazz; current != null; current = current.getEnclosingClass()) {
			if (current.isAnnotationPresent(UseEnvironment.class)) return Optional.of(current);
		}
		return Optional.empty();
	}

	private static Class<? extends Environment> parentOf(Class<? extends Environment> env) {
		EnvironmentParent annotation = env.getAnnotation(EnvironmentParent.class);
		return annotation != null ? annotation.value() : null;
	}

}
