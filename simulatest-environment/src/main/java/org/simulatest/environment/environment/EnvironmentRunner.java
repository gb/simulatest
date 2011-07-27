package org.simulatest.environment.environment;

import java.util.ArrayList;
import java.util.List;

import org.simulatest.environment.infra.EnvironmentExecutionException;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;


public class EnvironmentRunner {
	
	private EnvironmentFactory factory;
	private Tree<EnvironmentDefinition> tree;
	private List<EnvironmentRunnerListener> listeners;

	public EnvironmentRunner(EnvironmentFactory factory, Tree<EnvironmentDefinition> environmentTree) {
		this.factory = factory;
		this.tree = environmentTree;
		this.listeners = new ArrayList<EnvironmentRunnerListener>();
	}
	
	public EnvironmentRunner(EnvironmentFactory factory, EnvironmentTreeBuilder builder) {
		this(factory, builder.getTree());
	}

	public void addListener(EnvironmentRunnerListener listener) {
		listeners.add(listener);
	}
	
	public void removeListener(EnvironmentRunnerListener listener) {
		listeners.remove(listener);
	}
	
	private void fireBeforeRun(EnvironmentDefinition definition) {
		for (EnvironmentRunnerListener listener : listeners) listener.beforeRun(definition);
	}
	
	private void fireAfterRun(EnvironmentDefinition definition) {
		for (EnvironmentRunnerListener listener : listeners) listener.afterRun(definition);
	}
	
	private void fireAfterChildrenRun(EnvironmentDefinition definition) {
		for (EnvironmentRunnerListener listener : listeners) listener.afterChildrenRun(definition);
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
	}

	private void runEnvironment(EnvironmentDefinition definition) {
		fireBeforeRun(definition);
		executeEnvironment(definition);
		fireAfterRun(definition);
	}

	private void executeEnvironment(EnvironmentDefinition definition) {
		try {
			factory.create(definition).run();
		} catch (Exception exception) {
			String message = "Error in execution of Environment: " + definition.getName();
			throw new EnvironmentExecutionException(message, exception);
		}
	}
	
	private void fireAfterChildrenRunForParent(Node<EnvironmentDefinition> parent) {
		fireAfterChildrenRun(parent.getValue());
		if (parent.isLastChild()) fireAfterChildrenRunForParent(parent.getParent());
	}

}