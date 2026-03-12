package org.simulatest.environment.junit5;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
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
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.hierarchical.HierarchicalTestEngine;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.environment.EnvironmentDefinition;
import org.simulatest.environment.environment.EnvironmentRaker;
import org.simulatest.environment.environment.EnvironmentTreeBuilder;
import org.simulatest.environment.junit5.descriptor.ClassTestDescriptor;
import org.simulatest.environment.junit5.descriptor.EnvironmentTestDescriptor;
import org.simulatest.environment.junit5.descriptor.MethodTestDescriptor;
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

		EnvironmentRaker raker = new EnvironmentRaker(testClasses);
		EnvironmentTreeBuilder treeBuilder = new EnvironmentTreeBuilder(raker.getEnvironments());
		Tree<EnvironmentDefinition> envTree = treeBuilder.getTree();

		Map<EnvironmentDefinition, EnvironmentTestDescriptor> envDescriptors = new HashMap<>();

		for (var node : envTree) {
			EnvironmentDefinition def = node.getValue();

			EnvironmentTestDescriptor parentDesc = node.hasParent()
					? envDescriptors.get(node.getParentValue())
					: null;
			UniqueId parentId = (parentDesc != null) ? parentDesc.getUniqueId() : uniqueId;
			UniqueId envId = parentId.append("environment", def.getName());

			EnvironmentTestDescriptor envDesc = new EnvironmentTestDescriptor(envId, def);
			envDescriptors.put(def, envDesc);

			if (parentDesc != null) {
				parentDesc.addChild(envDesc);
			} else {
				engineDescriptor.addChild(envDesc);
			}

			if (raker.hasEnvironment(def)) {
				for (Class<?> testClass : raker.getTests(def)) {
					UniqueId classId = envId.append("class", testClass.getName());
					ClassTestDescriptor classDesc = new ClassTestDescriptor(classId, testClass);
					envDesc.addChild(classDesc);

					for (Method method : findTestMethods(testClass)) {
						UniqueId methodId = classId.append("method", method.getName());
						classDesc.addChild(new MethodTestDescriptor(methodId, testClass, method));
					}
				}
			}
		}

		return engineDescriptor;
	}

	private Set<Class<?>> collectTestClasses(EngineDiscoveryRequest request) {
		Set<Class<?>> testClasses = new LinkedHashSet<>();

		for (ClassSelector selector : request.getSelectorsByType(ClassSelector.class)) {
			Class<?> clazz = selector.getJavaClass();
			if (clazz.isAnnotationPresent(UseEnvironment.class)) {
				testClasses.add(clazz);
			}
		}

		for (PackageSelector selector : request.getSelectorsByType(PackageSelector.class)) {
			testClasses.addAll(scanForTestClasses(selector.getPackageName()));
		}

		return testClasses;
	}

	private Set<Class<?>> scanForTestClasses(String basePackage) {
		Set<Class<?>> result = new LinkedHashSet<>();
		try (var scanResult = new io.github.classgraph.ClassGraph()
				.enableClassInfo()
				.enableAnnotationInfo()
				.acceptPackages(basePackage.isEmpty() ? new String[0] : new String[]{basePackage})
				.scan()) {
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

	/**
	 * Discovers test methods by checking for annotations named "Test" (any framework).
	 * This avoids a compile-time dependency on JUnit Jupiter.
	 */
	private List<Method> findTestMethods(Class<?> testClass) {
		List<Method> testMethods = new ArrayList<>();
		for (Method method : testClass.getDeclaredMethods()) {
			for (Annotation annotation : method.getAnnotations()) {
				if ("Test".equals(annotation.annotationType().getSimpleName())) {
					testMethods.add(method);
					break;
				}
			}
		}
		return testMethods;
	}

}
