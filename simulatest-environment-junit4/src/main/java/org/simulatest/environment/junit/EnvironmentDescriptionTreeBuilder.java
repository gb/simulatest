package org.simulatest.environment.junit;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.junit.runner.Description;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;

public final class EnvironmentDescriptionTreeBuilder {

	private final Tree<EnvironmentDefinition> environments;
	private final Map<EnvironmentDefinition, Description> descriptions = new HashMap<>();

	public EnvironmentDescriptionTreeBuilder(Tree<EnvironmentDefinition> environments) {
		this.environments = Objects.requireNonNull(environments, "environments must not be null");
		createEnvironmentsDescriptions();
	}

	private void createEnvironmentsDescriptions() {
		for (Node<EnvironmentDefinition> node : environments)
			createEnvironmentDescription(node);
	}

	private void createEnvironmentDescription(Node<EnvironmentDefinition> node) {
		Description description = Description.createSuiteDescription(node.getValue().getEnvironmentClass());
		descriptions.put(node.getValue(), description);

		if (node.hasParent()) addTestDescription(node.getParentValue(), description);
	}

	public void addTestDescription(EnvironmentDefinition environment, Description description) {
		Description parent = descriptions.get(environment);
		if (parent == null) {
			throw new IllegalArgumentException("Unknown environment: " + environment.getName());
		}
		parent.addChild(description);
	}

	public Description getDescription() {
		return descriptions.get(environments.getRootValue());
	}

}