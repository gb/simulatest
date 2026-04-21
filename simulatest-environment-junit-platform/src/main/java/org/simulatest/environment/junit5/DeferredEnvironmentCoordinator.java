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
 * <p>Tracks two independent sets: environments that have been <i>claimed</i>
 * (first-time-seen gate) and environments whose savepoint push <i>succeeded</i>.
 * The lifecycle's {@code onExit} reads the second set so it pops only when a
 * push was actually recorded; a claim that never led to a push (environment
 * threw, push threw, a Quarkus restart wiped state, etc.) does not trigger a
 * spurious pop.
 *
 * <p>Suite-wide static state is acceptable here because Simulatest is
 * single-threaded: the Insistence Layer's single shared connection already
 * enforces that constraint upstream.
 */
public final class DeferredEnvironmentCoordinator {

	private static final Set<Class<? extends Environment>> claimed = new LinkedHashSet<>();
	private static final Set<Class<? extends Environment>> pushed = new LinkedHashSet<>();

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
		return walkParentChain(use.value());
	}

	/**
	 * Walks the {@code @EnvironmentParent} chain starting at {@code leaf},
	 * returning the chain root-first. Throws {@link IllegalStateException} if
	 * the chain is cyclic.
	 *
	 * <p>Package-private so tests can exercise cycle detection directly
	 * without needing a {@code @UseEnvironment}-annotated fixture, which the
	 * engine's classpath scanner would otherwise try to include in its tree.
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

	/**
	 * Marks {@code env} as already claimed in the current suite, so subsequent
	 * callers receive {@code false}.
	 *
	 * @return {@code true} if this call registered the environment; {@code false}
	 *         if another caller already did.
	 */
	public static synchronized boolean claimNotYetRun(Class<? extends Environment> env) {
		return claimed.add(env);
	}

	/**
	 * Records that {@code env} successfully had its Insistence Layer level
	 * pushed, so the matching {@code onExit} knows it's safe to pop.
	 */
	public static synchronized void recordPush(Class<? extends Environment> env) {
		pushed.add(env);
	}

	/**
	 * Returns whether {@code env}'s level was recorded as pushed. The lifecycle
	 * uses this to decide whether to pop on exit; a claim without a recorded
	 * push means the push never happened (failure, restart, etc.) and no pop
	 * should occur.
	 */
	public static synchronized boolean wasPushed(Class<? extends Environment> env) {
		return pushed.contains(env);
	}

	/**
	 * Clears tracking for {@code env}. Called by the lifecycle when the tree
	 * walk exits the environment's subtree: the savepoint has been popped and
	 * the environment will need to run again if somehow re-entered.
	 */
	public static synchronized void forget(Class<? extends Environment> env) {
		claimed.remove(env);
		pushed.remove(env);
	}

	/**
	 * Resets all tracking. Called between test sessions so the next suite starts
	 * clean, and when the Quarkus runtime restarts (e.g., {@code @TestProfile}
	 * switch) so stale "already run" records don't mask the new runtime's empty
	 * database.
	 */
	public static synchronized void reset() {
		claimed.clear();
		pushed.clear();
	}

	private static UseEnvironment resolveUseEnvironment(Class<?> testClass) {
		return UseEnvironmentClassScanner.resolveUseEnvironmentClass(testClass)
				.map(c -> c.getAnnotation(UseEnvironment.class))
				.orElse(null);
	}

	private static Class<? extends Environment> parentOf(Class<? extends Environment> env) {
		EnvironmentParent annotation = env.getAnnotation(EnvironmentParent.class);
		return annotation != null ? annotation.value() : null;
	}

}
