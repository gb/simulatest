package org.simulatest.environment;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.BiConsumer;

import org.simulatest.environment.infra.ExceptionAggregator;
import org.simulatest.environment.infra.ServiceLoaders;
import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
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
 * <p>When an Insistence Layer is given, each environment gets one level on
 * top of what it wrote, so its tests and its child environments can be undone
 * without losing it. An environment with a sibling after it has its own data
 * undone too, before that sibling runs. The data of the last sibling is left
 * for the parent to undo: the parent rolls back past it anyway.</p>
 *
 * <p>An environment that fails gets no {@code afterRun}, so its tests do not
 * run, and its child environments are skipped. By default the failure then
 * stops the run; {@link #onEnvironmentFailure(EnvironmentFailureHandler)}
 * lets the caller take the failure and let the run go on.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe. A runner is intended to be built
 * and invoked by the owning test runner on a single thread for the duration
 * of a suite run.</p>
 */
public final class EnvironmentRunner {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentRunner.class);

	private static final EnvironmentFailureHandler STOP_THE_RUN = (definition, failure) -> {
		throw failure;
	};

	private final EnvironmentFactory factory;
	private final Tree<EnvironmentDefinition> tree;
	private final List<EnvironmentRunnerListener> listeners;
	private final InsistenceLayer insistenceLayer;
	private EnvironmentFailureHandler failureHandler = STOP_THE_RUN;

	public EnvironmentRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		this.factory = Objects.requireNonNull(factory, "factory must not be null");
		this.tree = Objects.requireNonNull(environmentTree, "environmentTree must not be null");
		this.listeners = new ArrayList<>();
		this.insistenceLayer = null;
	}

	public EnvironmentRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree,
							 InsistenceLayer insistenceLayer) {
		this.factory = Objects.requireNonNull(factory, "factory must not be null");
		this.tree = Objects.requireNonNull(environmentTree, "environmentTree must not be null");
		this.listeners = new ArrayList<>();
		this.insistenceLayer = Objects.requireNonNull(insistenceLayer, "insistenceLayer must not be null");
	}

	public EnvironmentRunner(EnvironmentFactory factory, EnvironmentTreeBuilder builder) {
		this(factory, Objects.requireNonNull(builder, "builder must not be null").getTree());
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
		return ServiceLoaders.loadAtMostOne(EnvironmentFactory.class)
				.orElseThrow(() -> new EnvironmentInstantiationException(
						"META-INF/services environmentFactory was not found!"));
	}

	public Optional<InsistenceLayer> insistenceLayer() {
		return Optional.ofNullable(insistenceLayer);
	}

	// Listeners are kept sorted by phase, so infrastructure listeners fire before application ones.
	public void addListener(EnvironmentRunnerListener listener) {
		Objects.requireNonNull(listener, "listener must not be null");
		listeners.add(listener);
		listeners.sort(Comparator.comparing(EnvironmentRunnerListener::getPhase));
	}

	/**
	 * Sets who takes the failure of an environment. Without a handler the
	 * failure itself is thrown.
	 *
	 * @see EnvironmentFailureHandler
	 */
	public void onEnvironmentFailure(EnvironmentFailureHandler handler) {
		this.failureHandler = Objects.requireNonNull(handler, "handler must not be null");
	}

	public void run() {
		runOnItsOwnLevel(tree.getRootNode());
	}

	private void runOnItsOwnLevel(Node<EnvironmentDefinition> node) {
		SetupLevel level = newSetupLevel();
		executeWithCleanup(
				() -> runSubtree(node, level),
				() -> close(level, node));
	}

	// The level opens only when the environment ran, so a failed environment
	// never closes the level of its parent.
	private void runSubtree(Node<EnvironmentDefinition> node, SetupLevel level) {
		EnvironmentDefinition definition = node.getValue();
		logger.info("Running environment: {}", definition.getName());

		fireBeforeRun(definition);
		try {
			factory.createAndRun(definition);
		} catch (EnvironmentExecutionException failure) {
			failureHandler.environmentFailed(definition, failure);
			return;
		}
		level.open();
		runTestsAndChildrenOf(node);
	}

	// afterChildrenRun pairs with afterRun, so it fires even when a listener or
	// a child environment fails in between.
	private void runTestsAndChildrenOf(Node<EnvironmentDefinition> node) {
		executeWithCleanup(
				() -> fireAfterRunAndRunChildrenOf(node),
				() -> fireAfterChildrenRun(node.getValue()));
	}

	private void fireAfterRunAndRunChildrenOf(Node<EnvironmentDefinition> node) {
		fireAfterRun(node.getValue());
		for (Node<EnvironmentDefinition> child : node.getChildren()) runOnItsOwnLevel(child);
	}

	// Also when the environment failed halfway: what it wrote before failing is
	// undone too. When the undoing itself fails the run is stopping, and the
	// listeners are not told of a cleanup that did not happen.
	private void close(SetupLevel level, Node<EnvironmentDefinition> node) {
		if (!node.hasNextSibling()) {
			level.close();
			return;
		}

		level.closeUndoingSetup();
		fireAfterSiblingCleanup(node.getParent().getValue());
	}

	private SetupLevel newSetupLevel() {
		return SetupLevel.onLayerIfAny(insistenceLayer());
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
