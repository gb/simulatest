package org.simulatest.environment.junit5;

import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.platform.commons.support.ReflectionSupport;
import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.PackageSelector;
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
			Class<?> resolved = resolveUseEnvironmentClass(selector.getJavaClass());
			if (resolved != null) {
				result.add(resolved);
			}
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

		return result;
	}

	/**
	 * Walks the enclosing class chain looking for {@link UseEnvironment}.
	 * Returns the annotated class, or {@code null} if none is found.
	 */
	static Class<?> resolveUseEnvironmentClass(Class<?> clazz) {
		for (Class<?> c = clazz; c != null; c = c.getEnclosingClass()) {
			if (c.isAnnotationPresent(UseEnvironment.class)) return c;
		}
		return null;
	}

	private static boolean isAnnotated(Class<?> clazz) {
		return clazz.isAnnotationPresent(UseEnvironment.class);
	}

	private static void collectAnnotatedInnerClasses(Class<?> clazz, Set<Class<?>> result) {
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (inner.isAnnotationPresent(UseEnvironment.class)) {
				result.add(inner);
			}
			collectAnnotatedInnerClasses(inner, result);
		}
	}

}
