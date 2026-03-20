package org.simulatest.environment.tree;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.infra.exception.EnvironmentCyclicException;

public class EnvironmentTreeBuilder {
	
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
	
	private void addChild(EnvironmentDefinition definition, Set<EnvironmentDefinition> visited) {
		if (tree.contains(definition)) return;
		cyclicSanityTest(definition, visited);

		visited.add(definition);
		EnvironmentDefinition parentDefinition = EnvironmentDefinition.create(definition.getParentClass());

		if (!tree.contains(parentDefinition)) addChild(parentDefinition, visited);
		tree.addChild(parentDefinition, definition);
	}

	private void cyclicSanityTest(EnvironmentDefinition definition, Set<EnvironmentDefinition> visited) {
		if (!visited.contains(definition)) return;
		String message = String.format("The environment \"%s\" is cyclically referenced", definition.getName());
		throw new EnvironmentCyclicException(message);
	}
	
}