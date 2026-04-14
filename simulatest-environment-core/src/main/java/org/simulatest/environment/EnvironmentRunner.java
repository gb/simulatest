package org.simulatest.environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.function.BiConsumer;

import org.simulatest.environment.infra.ExceptionAggregator;
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

/**
 * Walks an {@link EnvironmentDefinition} tree, instantiating and running
 * each environment and firing listener callbacks at phase boundaries.
 *
 * <p><b>Thread-safety:</b> not thread-safe. A runner is intended to be built
 * and invoked by the owning test runner on a single thread for the duration
 * of a suite run.</p>
 */
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

	public Optional<InsistenceLayer> insistenceLayer() {
		return Optional.ofNullable(insistenceLayer);
	}

	// Listeners are kept sorted by phase so infrastructure listeners fire before user listeners.
	public void addListener(EnvironmentRunnerListener listener) {
		Objects.requireNonNull(listener, "listener must not be null");
		listeners.add(listener);
		listeners.sort(Comparator.comparing(EnvironmentRunnerListener::getPhase));
	}

	public void run() {
		runTree();
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

	private void fireEvent(EnvironmentDefinition definition, String eventName,
						   BiConsumer<EnvironmentRunnerListener, EnvironmentDefinition> action) {
		ExceptionAggregator failures = new ExceptionAggregator();
		for (EnvironmentRunnerListener listener : listeners) {
			try {
				action.accept(listener, definition);
			} catch (RuntimeException exception) {
				logger.error("Listener {} failed during {} for environment '{}'",
						listener.getClass().getSimpleName(), eventName, definition.getName(), exception);
				failures.add(exception);
			}
		}
		failures.throwIfAny(cause -> new EnvironmentExecutionException(
				"Failed during " + eventName + " for environment '" + definition.getName() + "'",
				cause));
	}

	private static void executeWithCleanup(Runnable body, Runnable cleanup) {
		ExceptionAggregator failures = new ExceptionAggregator();
		failures.capture(body);
		failures.capture(cleanup);
		failures.throwIfAny();
	}

}
