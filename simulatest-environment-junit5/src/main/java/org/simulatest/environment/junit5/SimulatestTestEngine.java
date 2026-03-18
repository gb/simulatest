package org.simulatest.environment.junit5;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;
import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.EnvironmentExtractor;
import org.simulatest.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.junit5.descriptor.EnvironmentTestDescriptor;
import org.simulatest.environment.junit5.descriptor.JupiterDelegatingClassDescriptor;
import org.simulatest.environment.tree.Node;
import org.simulatest.environment.tree.Tree;

/**
 * JUnit Platform {@link org.junit.platform.engine.TestEngine TestEngine} that orchestrates
 * test classes in environment-tree order with Insistence Layer savepoint management.
 *
 * <p>Extends {@link HierarchicalTestEngine} so the platform handles tree walking,
 * lifecycle notifications, and error handling. Each descriptor is a
 * {@link org.junit.platform.engine.support.hierarchical.Node Node} managing its own concerns.</p>
 */
public class SimulatestTestEngine extends HierarchicalTestEngine<SimulatestExecutionContext> {

	public static final String ENGINE_ID = "simulatest";

	private final UseEnvironmentClassScanner scanner = new UseEnvironmentClassScanner();

	@Override
	public String getId() {
		return ENGINE_ID;
	}

	@Override
	protected SimulatestExecutionContext createExecutionContext(ExecutionRequest request) {
		return SimulatestExecutionContext.EMPTY;
	}

	@Override
	public TestDescriptor discover(EngineDiscoveryRequest request, UniqueId uniqueId) {
		Set<Class<?>> testClasses = scanner.scan(request);
		SimulatestEngineDescriptor engineDescriptor = new SimulatestEngineDescriptor(uniqueId, testClasses);
		if (!testClasses.isEmpty()) populateDescriptors(engineDescriptor, testClasses);

		return engineDescriptor;
	}

	private void populateDescriptors(SimulatestEngineDescriptor engineDescriptor, Set<Class<?>> testClasses) {
		EnvironmentExtractor extractor = EnvironmentExtractor.extract(testClasses);
		Tree<EnvironmentDefinition> envTree = new EnvironmentTreeBuilder(extractor.getEnvironments()).getTree();

		buildDescriptorTree(engineDescriptor, envTree, extractor);
	}

	private void buildDescriptorTree(SimulatestEngineDescriptor engineDescriptor, Tree<EnvironmentDefinition> envTree,
									 EnvironmentExtractor extractor) {
		Map<EnvironmentDefinition, EnvironmentTestDescriptor> descriptorsByEnv = new HashMap<>();

		for (Node<EnvironmentDefinition> node : envTree) {
			TestDescriptor parent = resolveParent(node, engineDescriptor, descriptorsByEnv);
			EnvironmentTestDescriptor environmentDescriptor = createEnvironmentDescriptor(parent, node.getValue());

			descriptorsByEnv.put(node.getValue(), environmentDescriptor);
			parent.addChild(environmentDescriptor);
			addTestClassChildren(environmentDescriptor, extractor.getTests(node.getValue()));
		}
	}

	private TestDescriptor resolveParent(Node<EnvironmentDefinition> node,
			TestDescriptor root, Map<EnvironmentDefinition, EnvironmentTestDescriptor> descriptorsByEnv) {
		return node.hasParent() ? descriptorsByEnv.get(node.getParentValue()) : root;
	}

	private EnvironmentTestDescriptor createEnvironmentDescriptor(TestDescriptor parent, EnvironmentDefinition environmentDefinition) {
		UniqueId id = parent.getUniqueId()
				.append("environment", environmentDefinition.getEnvironmentClass().getName());
		return new EnvironmentTestDescriptor(id, environmentDefinition);
	}

	private void addTestClassChildren(EnvironmentTestDescriptor parent, List<Class<?>> testClasses) {
		for (Class<?> testClass : testClasses) {
			UniqueId classId = parent.getUniqueId().append("class", testClass.getName());
			parent.addChild(new JupiterDelegatingClassDescriptor(classId, testClass));
		}
	}

}
