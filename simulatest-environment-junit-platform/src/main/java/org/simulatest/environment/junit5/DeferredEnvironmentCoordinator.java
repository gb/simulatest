package org.simulatest.environment.junit5;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.simulatest.environment.Environment;
import org.simulatest.environment.annotation.EnvironmentParent;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * Cross-class coordination for deferred environment execution.
 *
 * <p>When a plugin contributes a {@link DeferredEnvironmentLifecycle} via
 * {@link org.simulatest.environment.plugin.SimulatestPlugin#environmentLifecycle()},
 * the Simulatest engine skips running environments in its tree walk. A
 * Jupiter extension shipped by the plugin then runs them from inside the
 * inner Jupiter session, after its DI container is ready. Since the inner
 * session is constructed per test class but the environment tree spans many
 * classes, shared state is needed so each environment runs exactly once and
 * later test classes don't re-seed state their ancestors already produced.
 *
 * <p>Suite-wide static state is acceptable here because Simulatest is
 * single-threaded: the Insistence Layer's single shared connection already
 * enforces that constraint upstream.
 */
public final class DeferredEnvironmentCoordinator {

	private static final Set<Class<? extends Environment>> runEnvironments = new LinkedHashSet<>();

	private DeferredEnvironmentCoordinator() {
	}

	/**
	 * Returns the environment ancestry for {@code testClass} root-first: the
	 * outermost ancestor is first, the class's leaf {@link UseEnvironment}
	 * last. Returns an empty list if neither {@code testClass} nor any of its
	 * enclosing classes carries {@code @UseEnvironment}.
	 *
	 * <p>Walks enclosing classes so {@code @Nested} inner classes inherit the
	 * outer class's environment. Detects cycles in the {@link EnvironmentParent}
	 * chain and fails loudly rather than looping.
	 */
	public static List<Class<? extends Environment>> ancestryOf(Class<?> testClass) {
		UseEnvironment use = resolveUseEnvironment(testClass);
		if (use == null) return List.of();

		List<Class<? extends Environment>> leafFirst = new ArrayList<>();
		Set<Class<? extends Environment>> visited = new LinkedHashSet<>();
		for (Class<? extends Environment> current = use.value(); current != null; current = parentOf(current)) {
			if (!visited.add(current)) {
				throw new IllegalStateException(
					"Cyclic @EnvironmentParent chain detected starting at " + use.value().getName()
					+ ": " + visited);
			}
			leafFirst.add(current);
		}
		Collections.reverse(leafFirst);
		return leafFirst;
	}

	/**
	 * Marks {@code env} as already run in the current suite, so subsequent
	 * callers receive {@code false} from {@link #claimNotYetRun(Class)}.
	 *
	 * @return {@code true} if this call registered the environment; {@code false}
	 *         if another caller already did.
	 */
	public static synchronized boolean claimNotYetRun(Class<? extends Environment> env) {
		return runEnvironments.add(env);
	}

	/**
	 * Clears tracking for {@code env}. Called by the engine when the tree walk
	 * exits the environment's subtree — at that point the savepoint has been
	 * popped and the environment will need to run again if somehow re-entered.
	 */
	public static synchronized void forget(Class<? extends Environment> env) {
		runEnvironments.remove(env);
	}

	/**
	 * Resets all tracking. Called between test sessions so the next suite starts
	 * clean, and when the Quarkus runtime restarts (e.g., {@code @TestProfile}
	 * switch) so stale "already run" records don't mask the new runtime's empty
	 * database.
	 */
	public static synchronized void reset() {
		runEnvironments.clear();
	}

	// Walks @Nested enclosing chain so an inner class inherits the outer
	// class's @UseEnvironment rather than coming back empty.
	private static UseEnvironment resolveUseEnvironment(Class<?> testClass) {
		for (Class<?> current = testClass; current != null; current = current.getEnclosingClass()) {
			UseEnvironment use = current.getAnnotation(UseEnvironment.class);
			if (use != null) return use;
		}
		return null;
	}

	private static Class<? extends Environment> parentOf(Class<? extends Environment> env) {
		EnvironmentParent annotation = env.getAnnotation(EnvironmentParent.class);
		return annotation != null ? annotation.value() : null;
	}

}
