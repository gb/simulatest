package org.simulatest.environment.junit5;

import java.util.Optional;

import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.UniqueId.Segment;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.engine.discovery.IterationSelector;
import org.junit.platform.engine.discovery.MethodSelector;
import org.junit.platform.engine.discovery.NestedClassSelector;
import org.junit.platform.engine.discovery.NestedMethodSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.discovery.UniqueIdSelector;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit5.descriptor.JupiterDelegatingClassDescriptor;

/**
 * Reads one JUnit Platform discovery request and finds the
 * {@link UseEnvironment @UseEnvironment} test classes it asks for.
 *
 * <p>A class with its own {@code @UseEnvironment} is selected as a whole when
 * the request names it, its package or its classpath root. Anything smaller
 * (a method, one invocation, a nested class without its own annotation, one
 * test this engine reported before) is selected as a part of the nearest
 * annotated class around it.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe; one scanner reads one request.</p>
 */
final class UseEnvironmentClassScanner {

	private final EngineDiscoveryRequest request;
	private final SelectedTests selected = new SelectedTests();

	UseEnvironmentClassScanner(EngineDiscoveryRequest request) {
		this.request = request;
	}

	SelectedTests scan() {
		selectClasses();
		selectNestedClasses();
		selectMethods();
		selectNestedMethods();
		selectIterations();
		selectUniqueIds();
		selectPackages();
		selectClasspathRoots();
		return selected;
	}

	private void selectClasses() {
		for (ClassSelector selector : request.getSelectorsByType(ClassSelector.class))
			selectClass(ClassChain.enclosing(selector.getJavaClass()), selector);
	}

	private void selectNestedClasses() {
		for (NestedClassSelector selector : request.getSelectorsByType(NestedClassSelector.class))
			selectClass(ClassChain.of(selector.getEnclosingClasses(), selector.getNestedClass()), selector);
	}

	private void selectMethods() {
		for (MethodSelector selector : request.getSelectorsByType(MethodSelector.class))
			selectPart(chainOf(selector), selector);
	}

	private void selectNestedMethods() {
		for (NestedMethodSelector selector : request.getSelectorsByType(NestedMethodSelector.class))
			selectPart(chainOf(selector), selector);
	}

	private void selectIterations() {
		for (IterationSelector selector : request.getSelectorsByType(IterationSelector.class))
			chainOfIterated(selector).ifPresent(chain -> selectPart(chain, selector));
	}

	private void selectUniqueIds() {
		for (UniqueIdSelector selector : request.getSelectorsByType(UniqueIdSelector.class))
			if (isReportedByThisEngine(selector.getUniqueId())) selectReported(selector.getUniqueId());
	}

	private void selectPackages() {
		for (PackageSelector selector : request.getSelectorsByType(PackageSelector.class))
			ReflectionSupport.findAllClassesInPackage(
					selector.getPackageName(), ClassChain::hasEnvironment, name -> true)
					.forEach(selected::addWholeClass);
	}

	private void selectClasspathRoots() {
		for (ClasspathRootSelector selector : request.getSelectorsByType(ClasspathRootSelector.class))
			ReflectionSupport.findAllClassesInClasspathRoot(
					selector.getClasspathRoot(), ClassChain::hasEnvironment, name -> true)
					.forEach(selected::addWholeClass);
	}

	// A class with its own environment runs as a whole. Any other class is a part
	// of the annotated class around it. Either way, the annotated classes inside
	// it run too, each under its own environment.
	private void selectClass(ClassChain chain, DiscoverySelector selector) {
		Class<?> selectedClass = chain.innermost();

		if (ClassChain.hasEnvironment(selectedClass)) selected.addWholeClass(selectedClass);
		else selectPart(chain, selector);

		selectClassesWithEnvironmentInside(selectedClass);
	}

	private void selectPart(ClassChain chain, DiscoverySelector selector) {
		chain.nearestWithEnvironment().ifPresent(owner -> selected.addPart(owner, selector));
	}

	private void selectClassesWithEnvironmentInside(Class<?> outer) {
		for (Class<?> inner : outer.getDeclaredClasses()) {
			if (ClassChain.hasEnvironment(inner)) selected.addWholeClass(inner);
			selectClassesWithEnvironmentInside(inner);
		}
	}

	private static ClassChain chainOf(MethodSelector selector) {
		return ClassChain.enclosing(selector.getJavaClass());
	}

	private static ClassChain chainOf(NestedMethodSelector selector) {
		return ClassChain.of(selector.getEnclosingClasses(), selector.getNestedClass());
	}

	private static Optional<ClassChain> chainOfIterated(IterationSelector selector) {
		DiscoverySelector iterated = selector.getParentSelector();
		if (iterated instanceof MethodSelector method) return Optional.of(chainOf(method));
		if (iterated instanceof NestedMethodSelector method) return Optional.of(chainOf(method));
		return Optional.empty();
	}

	private static boolean isReportedByThisEngine(UniqueId uniqueId) {
		return uniqueId.getEngineId().filter(SimulatestTestEngine.ENGINE_ID::equals).isPresent();
	}

	// The id of a class ends in a class segment. The id of one test ends in a test
	// segment, which holds the id Jupiter gave the test, right after the segment of
	// the class that ran it.
	private void selectReported(UniqueId uniqueId) {
		Segment last = uniqueId.getLastSegment();

		if (JupiterDelegatingClassDescriptor.CLASS_SEGMENT.equals(last.getType())) {
			classNamedBy(last).ifPresent(selected::addWholeClass);
		} else if (JupiterDelegatingClassDescriptor.TEST_SEGMENT.equals(last.getType())) {
			classNamedBy(uniqueId.removeLastSegment().getLastSegment()).ifPresent(owner ->
					selected.addPart(owner, DiscoverySelectors.selectUniqueId(last.getValue())));
		}
	}

	private static Optional<Class<?>> classNamedBy(Segment segment) {
		if (!JupiterDelegatingClassDescriptor.CLASS_SEGMENT.equals(segment.getType())) return Optional.empty();
		return ReflectionSupport.tryToLoadClass(segment.getValue()).toOptional()
				.filter(ClassChain::hasEnvironment);
	}

}
