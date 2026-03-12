package org.simulatest.environment.junit5;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.TestSource;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * Prunes {@link UseEnvironment @UseEnvironment} classes from Jupiter's test tree
 * so they are only run by the Simulatest engine.
 *
 * <p>Without this filter, both Jupiter and Simulatest would discover the same
 * {@code @UseEnvironment} test classes. Jupiter would run them without environment
 * setup, causing failures or polluting shared state.</p>
 *
 * <p>Registered automatically via
 * {@code META-INF/services/org.junit.platform.launcher.PostDiscoveryFilter}.</p>
 */
public class SimulatestPostDiscoveryFilter implements PostDiscoveryFilter {

	@Override
	public FilterResult apply(TestDescriptor descriptor) {
		if (!isJupiterDescriptor(descriptor)) {
			return FilterResult.includedIf(true);
		}

		Class<?> testClass = resolveTestClass(descriptor);
		if (testClass != null && testClass.isAnnotationPresent(UseEnvironment.class)) {
			return FilterResult.excluded("@UseEnvironment class is run by the Simulatest engine");
		}

		return FilterResult.includedIf(true);
	}

	private static Class<?> resolveTestClass(TestDescriptor descriptor) {
		TestSource source = descriptor.getSource().orElse(null);
		if (source instanceof ClassSource classSource) {
			return classSource.getJavaClass();
		}
		if (source instanceof MethodSource methodSource) {
			try {
				return Class.forName(methodSource.getClassName());
			} catch (ClassNotFoundException e) {
				return null;
			}
		}
		return null;
	}

	private static boolean isJupiterDescriptor(TestDescriptor descriptor) {
		return descriptor.getUniqueId().getSegments().stream()
				.anyMatch(s -> "engine".equals(s.getType()) && "junit-jupiter".equals(s.getValue()));
	}

}
