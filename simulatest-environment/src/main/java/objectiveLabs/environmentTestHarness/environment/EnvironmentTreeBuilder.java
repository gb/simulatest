package objectiveLabs.environmentTestHarness.environment;

import static objectiveLabs.environmentTestHarness.infra.AnnotationUtils.extractEnvironmentParent;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import objectiveLabs.environmentTestHarness.infra.EnvironmentCyclicException;
import objectiveLabs.environmentTestHarness.tree.Tree;

import com.google.common.base.Preconditions;

public class EnvironmentTreeBuilder  {
	
	private Tree<EnvironmentDefinition> tree;

	public EnvironmentTreeBuilder() {
		tree = new Tree<EnvironmentDefinition>(EnvironmentDefinition.bigBang());
	}

	public void add(EnvironmentDefinition definition) {
		Preconditions.checkNotNull(definition);
		addChild(definition, new LinkedList<EnvironmentDefinition>());
	}
	
	public void addAll(Collection<EnvironmentDefinition> definitions) {
		for (EnvironmentDefinition definition : definitions) add(definition);
	}
	
	public Tree<EnvironmentDefinition> getTree() {
		return tree;
	}
	
	private void addChild(EnvironmentDefinition definition, List<EnvironmentDefinition> environmentQueue) {
		if (tree.contains(definition)) return;
		cyclicSanityTest(definition, environmentQueue);
		
		environmentQueue.add(definition);
		EnvironmentDefinition parentDefinition = createParentDefinition(definition);
		
		if (!tree.contains(parentDefinition)) addChild(parentDefinition, environmentQueue);
		tree.addChild(parentDefinition, definition);
	}

	private void cyclicSanityTest(EnvironmentDefinition definition,	List<EnvironmentDefinition> environmentQueue) {
		if (!environmentQueue.contains(definition)) return;
		String message = String.format("The environment \"%s\" is cyclicity referenced", definition.getName());
		throw new EnvironmentCyclicException(message);
	}
	
	private EnvironmentDefinition createParentDefinition(EnvironmentDefinition definition) {
		return EnvironmentDefinition.create(extractEnvironmentParent(definition.getEnvironmentClass()));
	}
	
}