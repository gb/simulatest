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
			}
		}
		listeners.add(insertIndex, listener);
	}
	
	public void removeListener(EnvironmentRunnerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireBeforeRun(EnvironmentDefinition definition) {
		fireEvent(definition, EnvironmentRunnerListener::beforeRun);
	}

	private void fireAfterRun(EnvironmentDefinition definition) {
		fireEvent(definition, EnvironmentRunnerListener::afterRun);
	}

	private void fireAfterChildrenRun(EnvironmentDefinition definition) {
		fireEvent(definition, EnvironmentRunnerListener::afterChildrenRun);
	}

	private void fireAfterSiblingCleanup(EnvironmentDefinition definition) {
		fireEvent(definition, EnvironmentRunnerListener::afterSiblingCleanup);
	}

	private void fireEvent(EnvironmentDefinition definition, ListenerAction action) {
		RuntimeException firstException = null;
		for (EnvironmentRunnerListener listener : listeners) {
			try {
				action.execute(listener, definition);
			} catch (RuntimeException exception) {
				logger.error("Listener {} threw exception", listener.getClass().getName(), exception);
				if (firstException == null) firstException = exception;
			}
		}
		if (firstException != null) throw firstException;
	}

	@FunctionalInterface
	private interface ListenerAction {
		void execute(EnvironmentRunnerListener listener, EnvironmentDefinition definition);
	}
	
	public void run() {
		for (Node<EnvironmentDefinition> node : tree) run(node);
	}
	
	private void run(Node<EnvironmentDefinition> node) {
		runEnvironment(node.getValue());
		executeAfterEnvironment(node);
	}

	private void executeAfterEnvironment(Node<EnvironmentDefinition> node) {
		if (!node.getChildren().isEmpty()) return;
		fireAfterChildrenRun(node.getValue());
		if (node.isLastChild()) fireAfterChildrenRunForParent(node.getParent());
		else if (node.hasParent()) fireAfterSiblingCleanup(node.getParentValue());
	}

	private void runEnvironment(EnvironmentDefinition definition) {
		logger.info("[Run Environment] >> {}", definition.getName());
		
		fireBeforeRun(definition);
		if (!definition.equals(EnvironmentDefinition.bigBang())) executeEnvironment(definition);
		fireAfterRun(definition);
	}

	protected void executeEnvironment(EnvironmentDefinition definition) {
		try {
			factory.create(definition).run();
		} catch (Exception exception) {
			String message = "Error in execution of Environment: " + definition.getName();
			throw new EnvironmentExecutionException(message, exception);
		}
	}
	
	private void fireAfterChildrenRunForParent(Node<EnvironmentDefinition> parent) {
		fireAfterChildrenRun(parent.getValue());
		if (!parent.hasParent()) return;
		if (parent.isLastChild()) fireAfterChildrenRunForParent(parent.getParent());
		else fireAfterSiblingCleanup(parent.getParentValue());
	}

}