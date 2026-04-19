package org.simulatest.environment.listener;

import java.util.Objects;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * Bridges the environment runner lifecycle to the {@link InsistenceLayer}'s
 * savepoint stack so each environment subtree runs in its own rollback bubble.
 *
 * <p>The mapping is:</p>
 * <ul>
 *   <li>{@code afterRun}: push a new savepoint level (parent state is now visible,
 *       and any data the environment writes lives above it).</li>
 *   <li>{@code afterChildrenRun}: pop the level (or roll back its contents when
 *       it cannot be released), undoing everything written under this subtree.</li>
 *   <li>{@code afterSiblingCleanup}: roll the current level back to its starting
 *       state so the next sibling test/environment sees the same parent data
 *       without leaks from the previous one.</li>
 * </ul>
 *
 * <p>Runs in {@link ListenerPhase#INFRASTRUCTURE} so it fires before any
 * application listener observes the state.</p>
 */
public class EnvironmentRunnerListenerInsistence implements EnvironmentRunnerListener {

	private final InsistenceLayer insistenceLayer;

	public EnvironmentRunnerListenerInsistence(InsistenceLayer insistenceLayer) {
		this.insistenceLayer = Objects.requireNonNull(insistenceLayer, "insistenceLayer must not be null");
	}

	@Override
	public void afterRun(EnvironmentDefinition definition) {
		insistenceLayer.increaseLevel();
	}

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		insistenceLayer.decreaseLevelOrCleanup();
	}

	@Override
	public void afterSiblingCleanup(EnvironmentDefinition definition) {
		insistenceLayer.resetCurrentLevel();
	}

	@Override
	public ListenerPhase getPhase() {
		return ListenerPhase.INFRASTRUCTURE;
	}

}
