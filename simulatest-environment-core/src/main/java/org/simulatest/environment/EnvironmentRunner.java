package org.simulatest.environment;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.ServiceLoader;

import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.listener.EnvironmentRunnerListenerInsistence;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.infra.exception.EnvironmentGeneralException;
import org.simulatest.environment.tree.EnvironmentTreeBuilder;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;
import org.simulatest.insistencelayer.InsistenceLayer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class EnvironmentRunner {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentRunner.class);

	private final EnvironmentFactory factory;
	private final Tree<EnvironmentDefinition> tree;
	private final List<EnvironmentRunnerListener> listeners;
	private final InsistenceLayer insistenceLayer;

	public EnvironmentRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		this(factory, environmentTree, null);
	}

	public EnvironmentRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree,
							 InsistenceLayer insistenceLayer) {
		this.factory = Objects.requireNonNull(factory, "factory must not be null");
		this.tree = Objects.requireNonNull(environmentTree, "environmentTree must not be null");
		this.listeners = new ArrayList<>();
		this.insistenceLayer = insistenceLayer;
		if (insistenceLayer != null) {
			addListener(new EnvironmentRunnerListenerInsistence(insistenceLayer));
		}
	}

	public EnvironmentRunner(EnvironmentFactory factory, EnvironmentTreeBuilder builder) {
		this(factory, builder.getTree());
	}

	public static void runEnvironment(Class<? extends Environment> environment) {
		runEnvironment(environment, loadFactory());
	}

	public static void runEnvironment(Class<? extends Environment> environment, EnvironmentFactory factory) {
		EnvironmentTreeBuilder builder = new EnvironmentTreeBuilder();
		builder.add(EnvironmentDefinition.create(environment));
		new EnvironmentRunner(factory, builder).run();
	}

	private static EnvironmentFactory loadFactory() {
		return ServiceLoader.load(EnvironmentFactory.class)
				.findFirst()
				.orElseThrow(() -> new EnvironmentGeneralException(
						"META-INF/services environmentFactory was not found!"));
	}

	public InsistenceLayer insistenceLayer() {
		return insistenceLayer;
	}

	// Listeners are kept sorted by phase so infrastructure listeners fire before user listeners.
	public void addListener(EnvironmentRunnerListener listener) {
		Objects.requireNonNull(listener, "listener must not be null");
		int insertIndex = 0;
		for (int i = 0; i < listeners.size(); i++) {
			if (listeners.get(i).getPhase().compareTo(listener.getPhase()) <= 0) {
				insertIndex = i + 1;
			} else {
				break;
			}
		}
		listeners.add(insertIndex, listener);
	}

	public void run() {
		if (insistenceLayer != null) {
			runWithInsistence();
		} else {
			runTree();
		}
	}

	// Manages the outermost insistence level (suite scope).
	// Per-environment level changes are handled by EnvironmentRunnerListenerInsistence.
	private void runWithInsistence() {
		insistenceLayer.increaseLevel();

		try {
			runTree();
			insistenceLayer.decreaseLevel();
		} catch (RuntimeException exception) {
			try {
				insistenceLayer.decreaseAllLevels();
			} catch (RuntimeException cleanupException) {
				logger.error("Checkpoint cleanup failed after environment failure", cleanupException);
				exception.addSuppressed(cleanupException);
			}
			throw exception;
		}
	}

	private void runTree() {
		for (Node<EnvironmentDefinition> node : tree) runNode(node);
	}

	private void runNode(Node<EnvironmentDefinition> node) {
		executeWithCleanup(
				() -> runEnvironmentLifecycle(node.getValue()),
				() -> fireLeafCallbacks(node));
	}

	private void runEnvironmentLifecycle(EnvironmentDefinition definition) {
		logger.info("Running environment: {}", definition.getName());

		fireBeforeRun(definition);
		executeWithCleanup(
				() -> executeEnvironment(definition),
				() -> fireAfterRun(definition));
	}

	private void executeEnvironment(EnvironmentDefinition definition) {
		try {
			factory.create(definition).run();
		} catch (Exception exception) {
			throw new EnvironmentExecutionException(
					"Failed during run for environment '" + definition.getName() + "'", exception);
		}
	}

	// After visiting a leaf, propagate "after children" callbacks up the tree
	// for any ancestor whose subtree is now fully visited (i.e., the leaf is
	// the last child at each level). Non-last siblings get a reset instead.
	private void fireLeafCallbacks(Node<EnvironmentDefinition> node) {
		if (!node.isLeaf()) return;

		executeWithCleanup(
				() -> fireAfterChildrenRun(node.getValue()),
				() -> fireParentBoundaryCallbacks(node));
	}

	private void fireParentBoundaryCallbacks(Node<EnvironmentDefinition> node) {
		if (!node.hasParent()) return;

		if (node.isLastChild()) propagateAfterChildrenRun(node.getParent());
		else fireAfterSiblingCleanup(node.getParentValue());
	}

	private void propagateAfterChildrenRun(Node<EnvironmentDefinition> parent) {
		executeWithCleanup(
				() -> fireAfterChildrenRun(parent.getValue()),
				() -> fireParentBoundaryCallbacks(parent));
	}

	private void fireBeforeRun(EnvironmentDefinition definition) {
		fireEvent(definition, "beforeRun", EnvironmentRunnerListener::beforeRun);
	}

	private void fireAfterRun(EnvironmentDefinition definition) {
		fireEvent(definition, "afterRun", EnvironmentRunnerListener::afterRun);
	}

	private void fireAfterChildrenRun(EnvironmentDefinition definition) {
		fireEvent(definition, "afterChildrenRun", EnvironmentRunnerListener::afterChildrenRun);
	}

	private void fireAfterSiblingCleanup(EnvironmentDefinition definition) {
		fireEvent(definition, "afterSiblingCleanup", EnvironmentRunnerListener::afterSiblingCleanup);
	}

	private void fireEvent(EnvironmentDefinition definition, String eventName, ListenerAction action) {
		RuntimeException firstException = null;
		for (EnvironmentRunnerListener listener : listeners) {
			try {
				action.execute(listener, definition);
			} catch (RuntimeException exception) {
				logger.error("Listener {} failed during {} for environment '{}'",
						listener.getClass().getSimpleName(), eventName, definition.getName(), exception);
				if (firstException == null) firstException = exception;
				else firstException.addSuppressed(exception);
			}
		}
		if (firstException != null) {
			throw new EnvironmentExecutionException(
					"Failed during " + eventName + " for environment '" + definition.getName() + "'",
					firstException);
		}
	}

	@FunctionalInterface
	private interface ListenerAction {
		void execute(EnvironmentRunnerListener listener, EnvironmentDefinition definition);
	}

	private static void executeWithCleanup(Runnable first, Runnable second) {
		RuntimeException failure = null;
		try {
			first.run();
		} catch (RuntimeException e) {
			failure = e;
		}
		try {
			second.run();
		} catch (RuntimeException e) {
			if (failure != null) failure.addSuppressed(e);
			else failure = e;
		}
		if (failure != null) throw failure;
	}

}
