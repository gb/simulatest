package org.simulatest.environment.junit5;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.ExecutionRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentExtractor;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.junit5.descriptor.EnvironmentTestDescriptor;
import org.simulatest.environment.junit5.descriptor.JupiterDelegatingClassDescriptor;
import org.simulatest.environment.tree.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * JUnit Platform {@link org.junit.platform.engine.TestEngine TestEngine} that orchestrates
 * test classes in environment-tree order with Insistence Layer savepoint management.
 *
 * <p>Extends {@link HierarchicalTestEngine} so the platform handles tree walking,
 * lifecycle notifications, and error handling. Each descriptor is a
 * {@link org.junit.platform.engine.support.hierarchical.Node Node} managing its own concerns.</p>
 */
public class SimulatestTestEngine extends HierarchicalTestEngine<SimulatestExecutionContext> {

	private static final Logger logger = LoggerFactory.getLogger(SimulatestTestEngine.class);

	public static final String ENGINE_ID = "simulatest";

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
		SimulatestEngineDescriptor engineDescriptor = new SimulatestEngineDescriptor(uniqueId);

		Set<Class<?>> testClasses = collectTestClasses(request);
		engineDescriptor.setTestClasses(testClasses);
		if (testClasses.isEmpty()) return engineDescriptor;

		EnvironmentExtractor extractor = new EnvironmentExtractor(testClasses);
		Tree<EnvironmentDefinition> envTree = new EnvironmentTreeBuilder(extractor.getEnvironments()).getTree();

		buildDescriptorTree(engineDescriptor, envTree, extractor);

		return engineDescriptor;
	}

	private void buildDescriptorTree(SimulatestEngineDescriptor engineDescriptor,
			Tree<EnvironmentDefinition> envTree, EnvironmentExtractor extractor) {
		Map<EnvironmentDefinition, EnvironmentTestDescriptor> envDescriptors = new HashMap<>();

		for (var node : envTree) {
			EnvironmentTestDescriptor parentDesc = node.hasParent()
					? envDescriptors.get(node.getParentValue())
					: null;

			EnvironmentTestDescriptor envDesc = createEnvironmentDescriptor(
					node.getValue(), parentDesc, engineDescriptor.getUniqueId());
			envDescriptors.put(node.getValue(), envDesc);

			if (parentDesc != null) {
				parentDesc.addChild(envDesc);
			} else {
				engineDescriptor.addChild(envDesc);
			}

			if (extractor.hasEnvironment(node.getValue())) {
				addTestClassDescriptors(envDesc, extractor.getTests(node.getValue()));
			}
		}
	}

	private EnvironmentTestDescriptor createEnvironmentDescriptor(
			EnvironmentDefinition def, EnvironmentTestDescriptor parentDesc, UniqueId engineId) {
		UniqueId parentId = (parentDesc != null) ? parentDesc.getUniqueId() : engineId;
		return new EnvironmentTestDescriptor(parentId.append("environment", def.getName()), def);
	}

	private void addTestClassDescriptors(EnvironmentTestDescriptor envDesc, List<Class<?>> testClasses) {
		for (Class<?> testClass : testClasses) {
			UniqueId classId = envDesc.getUniqueId().append("class", testClass.getName());
			envDesc.addChild(new JupiterDelegatingClassDescriptor(classId, testClass));
		}
	}

	private Set<Class<?>> collectTestClasses(EngineDiscoveryRequest request) {
		Set<Class<?>> testClasses = new LinkedHashSet<>();

		for (ClassSelector selector : request.getSelectorsByType(ClassSelector.class)) {
			Class<?> resolved = resolveUseEnvironmentClass(selector.getJavaClass());
			if (resolved != null) {
				testClasses.add(resolved);
			}
			collectAnnotatedInnerClasses(selector.getJavaClass(), testClasses);
		}

		for (PackageSelector selector : request.getSelectorsByType(PackageSelector.class)) {
			testClasses.addAll(scanForTestClasses(selector.getPackageName()));
		}

		for (ClasspathRootSelector selector : request.getSelectorsByType(ClasspathRootSelector.class)) {
			testClasses.addAll(scanClasspathRoot(selector));
		}

		return testClasses;
	}

	private static void collectAnnotatedInnerClasses(Class<?> clazz, Set<Class<?>> result) {
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (inner.isAnnotationPresent(UseEnvironment.class)) {
				result.add(inner);
			}
			collectAnnotatedInnerClasses(inner, result);
		}
	}

	static Class<?> resolveUseEnvironmentClass(Class<?> clazz) {
		for (Class<?> c = clazz; c != null; c = c.getEnclosingClass()) {
			if (c.isAnnotationPresent(UseEnvironment.class)) return c;
		}
		return null;
	}

	private Set<Class<?>> scanClasspathRoot(ClasspathRootSelector selector) {
		Set<Class<?>> result = new LinkedHashSet<>();
		URI root = selector.getClasspathRoot();

		try {
			Path rootPath = Paths.get(root);
			try (var stream = Files.walk(rootPath)) {
				stream.filter(p -> p.toString().endsWith(".class"))
						.forEach(p -> {
							String relative = rootPath.relativize(p).toString();
							String className = relative
									.replace(File.separatorChar, '.')
									.replace('/', '.')
									.replace(".class", "");
							try {
								Class<?> clazz = Class.forName(className);
								if (clazz.isAnnotationPresent(UseEnvironment.class)) {
									result.add(clazz);
								}
								collectAnnotatedInnerClasses(clazz, result);
							} catch (ClassNotFoundException | NoClassDefFoundError e) {
								logger.debug("Skipping class: {}", className, e);
							}
						});
			}
		} catch (Exception e) {
			logger.warn("Failed to scan classpath root: {}", root, e);
		}

		return result;
	}

	private Set<Class<?>> scanForTestClasses(String basePackage) {
		return scanWithClassGraph(new io.github.classgraph.ClassGraph()
				.acceptPackages(basePackage.isEmpty() ? new String[0] : new String[]{basePackage}));
	}

	private Set<Class<?>> scanWithClassGraph(io.github.classgraph.ClassGraph classGraph) {
		Set<Class<?>> result = new LinkedHashSet<>();
		try (var scanResult = classGraph.enableClassInfo().enableAnnotationInfo().ignoreClassVisibility().scan()) {
			for (var classInfo : scanResult.getClassesWithAnnotation(UseEnvironment.class)) {
				try {
					result.add(classInfo.loadClass());
				} catch (Exception e) {
					logger.warn("Could not load class: {}", classInfo.getName(), e);
				}
			}
		}
		return result;
	}

}
