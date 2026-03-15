package org.simulatest.environment.environment;

import java.util.ArrayList;
import java.util.List;

import org.simulatest.environment.environment.listener.EnvironmentRunnerListener;
import org.simulatest.environment.infra.exception.EnvironmentExecutionException;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentRunner {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvironmentRunner.class);

	private final EnvironmentFactory factory;
	private final Tree<EnvironmentDefinition> tree;
	private final List<EnvironmentRunnerListener> listeners;

	public EnvironmentRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		this.factory = factory;
		this.tree = environmentTree;
		this.listeners = new ArrayList<>();
	}
	
	public EnvironmentRunner(EnvironmentFactory factory, EnvironmentTreeBuilder builder) {
		this(factory, builder.getTree());
	}

	public void addListener(EnvironmentRunnerListener listener) {
		int insertIndex = 0;
		for (int i = 0; i < listeners.size(); i++) {
			if (listeners.get(i).getPhase().ordinal() <= listener.getPhase().ordinal()) {
				insertIndex = i + 1;
			} else {
				break;
			}
		}
		listeners.add(insertIndex, listener);
	}
	
	public void removeListener(EnvironmentRunnerListener listener) {
		listeners.remove(listener);
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
	
	public void run() {
		for (Node<EnvironmentDefinition> node : tree) run(node);
	}
	
	private void run(Node<EnvironmentDefinition> node) {
		RuntimeException failure = null;
		try {
			runEnvironment(node.getValue());
		} catch (RuntimeException e) {
			failure = e;
		}
		try {
			executeAfterEnvironment(node);
		} catch (RuntimeException e) {
			if (failure != null) failure.addSuppressed(e);
			else failure = e;
		}
		if (failure != null) throw failure;
	}

	private void executeAfterEnvironment(Node<EnvironmentDefinition> node) {
		if (!node.getChildren().isEmpty()) return;
		RuntimeException failure = null;
		try {
			fireAfterChildrenRun(node.getValue());
		} catch (RuntimeException e) {
			failure = e;
		}
		try {
			if (node.isLastChild()) fireAfterChildrenRunForParent(node.getParent());
			else if (node.hasParent()) fireAfterSiblingCleanup(node.getParentValue());
		} catch (RuntimeException e) {
			if (failure != null) failure.addSuppressed(e);
			else failure = e;
		}
		if (failure != null) throw failure;
	}

	private void runEnvironment(EnvironmentDefinition definition) {
		logger.info("[Run Environment] >> {}", definition.getName());

		fireBeforeRun(definition);
		RuntimeException failure = null;
		try {
			if (!definition.equals(EnvironmentDefinition.bigBang())) executeEnvironment(definition);
		} catch (RuntimeException e) {
			failure = e;
		}
		try {
			fireAfterRun(definition);
		} catch (RuntimeException e) {
			if (failure != null) failure.addSuppressed(e);
			else failure = e;
		}
		if (failure != null) throw failure;
	}

	protected void executeEnvironment(EnvironmentDefinition definition) {
		try {
			factory.create(definition).run();
		} catch (Exception exception) {
			throw new EnvironmentExecutionException(
					"Failed during run for environment '" + definition.getName() + "'", exception);
		}
	}
	
	private void fireAfterChildrenRunForParent(Node<EnvironmentDefinition> parent) {
		RuntimeException failure = null;
		try {
			fireAfterChildrenRun(parent.getValue());
		} catch (RuntimeException e) {
			failure = e;
		}
		if (parent.hasParent()) {
			try {
				if (parent.isLastChild()) fireAfterChildrenRunForParent(parent.getParent());
				else fireAfterSiblingCleanup(parent.getParentValue());
			} catch (RuntimeException e) {
				if (failure != null) failure.addSuppressed(e);
				else failure = e;
			}
		}
		if (failure != null) throw failure;
	}

}