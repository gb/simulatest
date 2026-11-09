package org.simulatest.environment.junit5;

import java.util.LinkedHashSet;
import java.util.Optional;
import java.util.Set;

import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * Finds test classes annotated with {@link UseEnvironment} from JUnit Platform
 * discovery selectors.
 *
 * <p>Handles three selector types:</p>
 * <ul>
 *   <li>{@link ClassSelector} -- checks the class and its enclosing hierarchy</li>
 *   <li>{@link PackageSelector} -- scans via {@link ReflectionSupport}</li>
 *   <li>{@link ClasspathRootSelector} -- scans via {@link ReflectionSupport}</li>
 * </ul>
 */
class UseEnvironmentClassScanner {

	Set<Class<?>> scan(EngineDiscoveryRequest request) {
		Set<Class<?>> result = new LinkedHashSet<>();

		for (ClassSelector selector : request.getSelectorsByType(ClassSelector.class)) {
			resolveUseEnvironmentClass(selector.getJavaClass()).ifPresent(result::add);
			collectAnnotatedInnerClasses(selector.getJavaClass(), result);
		}

		for (PackageSelector selector : request.getSelectorsByType(PackageSelector.class)) {
			result.addAll(ReflectionSupport.findAllClassesInPackage(
					selector.getPackageName(), UseEnvironmentClassScanner::isAnnotated, name -> true));
		}

		for (ClasspathRootSelector selector : request.getSelectorsByType(ClasspathRootSelector.class)) {
			result.addAll(ReflectionSupport.findAllClassesInClasspathRoot(
					selector.getClasspathRoot(), UseEnvironmentClassScanner::isAnnotated, name -> true));
		}

		return Set.copyOf(result);
	}

	/**
	 * Walks the enclosing class chain looking for {@link UseEnvironment}.
	 */
	static Optional<Class<?>> resolveUseEnvironmentClass(Class<?> clazz) {
		for (Class<?> currentClass = clazz; currentClass != null; currentClass = currentClass.getEnclosingClass())
			if (currentClass.isAnnotationPresent(UseEnvironment.class))
				return Optional.of(currentClass);

		return Optional.empty();
	}

	/**
	 * Resolves the {@link UseEnvironment} class the descriptor's test class
	 * belongs to, or empty when the descriptor has no class or method source
	 * or no enclosing class is annotated.
	 */
	static Optional<Class<?>> resolveUseEnvironmentClass(TestDescriptor descriptor) {
		return resolveTestClass(descriptor).flatMap(UseEnvironmentClassScanner::resolveUseEnvironmentClass);
	}

	private static Optional<Class<?>> resolveTestClass(TestDescriptor descriptor) {
		return descriptor.getSource().map(source -> {
			if (source instanceof ClassSource s) return s.getJavaClass();
			if (source instanceof MethodSource s) return s.getJavaClass();
			return null;
		});
	}

	private static boolean isAnnotated(Class<?> clazz) {
		return clazz.isAnnotationPresent(UseEnvironment.class);
	}

	private static void collectAnnotatedInnerClasses(Class<?> clazz, Set<Class<?>> result) {
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (inner.isAnnotationPresent(UseEnvironment.class)) result.add(inner);
			collectAnnotatedInnerClasses(inner, result);
		}
	}

}
