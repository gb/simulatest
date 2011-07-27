package org.simulatest.environment.junit;

import java.util.HashMap;
import java.util.Map;


import org.junit.runner.Description;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;


public class EnvironmentDescriptionTreeBuilder {

	private Tree<EnvironmentDefinition> environments;
	private Map<EnvironmentDefinition, Description> descriptions = new HashMap<EnvironmentDefinition, Description>();

	public EnvironmentDescriptionTreeBuilder(Tree<EnvironmentDefinition> environments) {
		this.environments = environments;
		createEnvironmentsDescriptions();
	}

	private void createEnvironmentsDescriptions() {
		for (Node<EnvironmentDefinition> node : environments) createEnvironmentDescription(node);
	}

	private void createEnvironmentDescription(Node<EnvironmentDefinition> node) {
		Description description = Description.createSuiteDescription(node.getValue().getEnvironmentClass());
		descriptions.put(node.getValue(), description);

		if (node.hasParent()) addTestDescription(node.getParentValue(), description);
	}

	public void addTestDescription(EnvironmentDefinition environment, Description description) {
		descriptions.get(environment).addChild(description);
	}

	public Description getDescription() {
		return descriptions.get(environments.getRootValue());
	}

}