package org.simulatest.environment.junit5;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedHashSet;
import java.util.Set;

import org.junit.platform.engine.EngineDiscoveryRequest;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.ClasspathRootSelector;
import org.junit.platform.engine.discovery.PackageSelector;
import org.simulatest.environment.annotation.UseEnvironment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Finds test classes annotated with {@link UseEnvironment} from JUnit Platform
 * discovery selectors.
 *
 * <p>Handles three selector types:</p>
 * <ul>
 *   <li>{@link ClassSelector} -- checks the class and its enclosing hierarchy</li>
 *   <li>{@link PackageSelector} -- scans via ClassGraph</li>
 *   <li>{@link ClasspathRootSelector} -- walks the file system directly
 *       (ClassGraph's {@code overrideClasspath} ignores class visibility)</li>
 * </ul>
 */
class UseEnvironmentClassScanner {

	private static final Logger logger = LoggerFactory.getLogger(UseEnvironmentClassScanner.class);

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
			result.addAll(scanPackage(selector.getPackageName()));
		}

		for (ClasspathRootSelector selector : request.getSelectorsByType(ClasspathRootSelector.class)) {
			result.addAll(scanClasspathRoot(selector.getClasspathRoot()));
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

	private static void collectAnnotatedInnerClasses(Class<?> clazz, Set<Class<?>> result) {
		for (Class<?> inner : clazz.getDeclaredClasses()) {
			if (inner.isAnnotationPresent(UseEnvironment.class)) {
				result.add(inner);
			}
			collectAnnotatedInnerClasses(inner, result);
		}
	}

	private Set<Class<?>> scanClasspathRoot(URI root) {
		Set<Class<?>> result = new LinkedHashSet<>();

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

	private Set<Class<?>> scanPackage(String basePackage) {
		Set<Class<?>> result = new LinkedHashSet<>();
		try (var scanResult = new io.github.classgraph.ClassGraph()
				.acceptPackages(basePackage.isEmpty() ? new String[0] : new String[]{basePackage})
				.enableClassInfo()
				.enableAnnotationInfo()
				.ignoreClassVisibility()
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

}
