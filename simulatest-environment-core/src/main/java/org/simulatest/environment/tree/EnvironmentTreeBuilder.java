package org.simulatest.environment.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.infra.exception.EnvironmentCyclicException;

public final class EnvironmentTreeBuilder {
	
	private final Tree<EnvironmentDefinition> tree;

	public EnvironmentTreeBuilder() {
		this.tree = new Tree<>(EnvironmentDefinition.bigBang());
	}
	
	public EnvironmentTreeBuilder(Collection<EnvironmentDefinition> environments) {
		this();
		this.addAll(environments);
	}

	public void add(EnvironmentDefinition definition) {
		Objects.requireNonNull(definition);
		addChild(definition, new HashSet<>());
	}
	
	public void addAll(Collection<EnvironmentDefinition> definitions) {
		for (EnvironmentDefinition definition : definitions) add(definition);
	}
	
	public Tree<EnvironmentDefinition> getTree() {
		return tree;
	}
	
	// Recursively walks up the parent chain, inserting ancestors before descendants.
	// The visited set detects cycles in the @EnvironmentParent graph.
	private void addChild(EnvironmentDefinition definition, Set<EnvironmentDefinition> visited) {
		if (tree.contains(definition)) return;
		throwIfCyclic(definition, visited);

		visited.add(definition);
		EnvironmentDefinition parent = definition.createParentDefinition();

		if (!tree.contains(parent)) addChild(parent, visited);
		tree.addChild(parent, definition);
	}

	private void throwIfCyclic(EnvironmentDefinition definition, Set<EnvironmentDefinition> visited) {
		if (!visited.contains(definition)) return;
		String message = String.format("The environment \"%s\" is cyclically referenced (visited: %s)", definition.getName(), visited);
		throw new EnvironmentCyclicException(message);
	}
	
}