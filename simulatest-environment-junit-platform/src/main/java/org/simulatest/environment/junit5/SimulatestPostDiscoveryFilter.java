package org.simulatest.environment.junit5;

import java.util.Optional;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.descriptor.MethodSource;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * Prunes {@link UseEnvironment @UseEnvironment} classes from non-Simulatest engines
 * so they are only run by the Simulatest engine.
 *
 * <p>Without this filter, other engines (e.g. Jupiter) would also discover
 * {@code @UseEnvironment} test classes and run them without environment
 * setup, causing failures or polluting shared state.</p>
 *
 * <p>Registered automatically via
 * {@code META-INF/services/org.junit.platform.launcher.PostDiscoveryFilter}.</p>
 */
public final class SimulatestPostDiscoveryFilter implements PostDiscoveryFilter {

	@Override
	public FilterResult apply(TestDescriptor descriptor) {
		if (!isExternalEngine(descriptor)) {
			return FilterResult.included("Simulatest engine descriptor");
		}

		if (belongsToUseEnvironmentClass(descriptor)) {
			return FilterResult.excluded("@UseEnvironment class is run by the Simulatest engine");
		}

		return FilterResult.included("No @UseEnvironment annotation");
	}

	private static boolean belongsToUseEnvironmentClass(TestDescriptor descriptor) {
		return resolveTestClass(descriptor)
				.flatMap(UseEnvironmentClassScanner::resolveUseEnvironmentClass)
				.isPresent();
	}

	private static Optional<Class<?>> resolveTestClass(TestDescriptor descriptor) {
		return descriptor.getSource().map(source -> {
			if (source instanceof ClassSource s) return s.getJavaClass();
			if (source instanceof MethodSource s) return s.getJavaClass();
			return null;
		});
	}

	private static boolean isExternalEngine(TestDescriptor descriptor) {
		return descriptor.getUniqueId().getEngineId()
				.filter(id -> !SimulatestTestEngine.ENGINE_ID.equals(id))
				.isPresent();
	}

}
