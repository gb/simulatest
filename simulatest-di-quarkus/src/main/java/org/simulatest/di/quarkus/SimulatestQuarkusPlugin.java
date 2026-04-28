package org.simulatest.di.quarkus;

import org.simulatest.environment.plugin.DependencyInjectionPlugin;
import org.simulatest.environment.plugin.EnvironmentLifecycle;

/**
 * Simulatest plugin for Quarkus + Panache test suites.
 *
 * <p>Wiring is the canonical one-liner shared with the other DI modules.
 * The interesting work happens elsewhere:
 * <ul>
 *   <li>{@link SimulatestQuarkusTestResource} configures the Insistence
 *       Layer and applies the user schema before Quarkus boots Arc.</li>
 *   <li>{@link QuarkusContext} resolves environment beans from Arc once
 *       Arc is up.</li>
 *   <li>{@link PostArcEnvironmentRunner} runs the environment ancestry
 *       inside {@code @QuarkusTest}'s {@code beforeAll}, after Arc has
 *       booted so {@code @Inject} fields populate.</li>
 * </ul>
 *
 * @see org.simulatest.di.quarkus
 */
public final class SimulatestQuarkusPlugin extends DependencyInjectionPlugin {

	public SimulatestQuarkusPlugin() {
		super(new QuarkusContext());
	}

	/**
	 * Defers the engine's tree-walk hooks. Environment instantiation and
	 * savepoint placement happen in {@link PostArcEnvironmentRunner} once
	 * Arc has booted inside {@code @QuarkusTest}'s lifecycle.
	 */
	@Override
	public EnvironmentLifecycle environmentLifecycle() {
		return DeferredEnvironmentLifecycle.INSTANCE;
	}

}
