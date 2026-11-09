package org.simulatest.environment.junit5;

import java.util.Objects;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.launcher.PostDiscoveryFilter;

/**
 * Keeps an inner Jupiter run to the tests that belong to one
 * {@link org.simulatest.environment.annotation.UseEnvironment @UseEnvironment}
 * class.
 *
 * <p>Jupiter discovers every {@code @Nested} class of the selected class. A
 * nested class with its own {@code @UseEnvironment} gets its own descriptor
 * from the Simulatest engine, so this filter drops its tests from the outer
 * class's run. Otherwise they would run twice, the first time under the
 * outer environment.</p>
 */
public final class OwnEnvironmentClassFilter implements PostDiscoveryFilter {

	private final Class<?> environmentClass;

	/**
	 * @param environmentClass the {@code @UseEnvironment} class whose tests this run owns
	 */
	public OwnEnvironmentClassFilter(Class<?> environmentClass) {
		this.environmentClass = Objects.requireNonNull(environmentClass, "environmentClass must not be null");
	}

	@Override
	public FilterResult apply(TestDescriptor descriptor) {
		return UseEnvironmentClassScanner.resolveUseEnvironmentClass(descriptor)
				.filter(owner -> owner != environmentClass)
				.map(owner -> FilterResult.excluded(owner.getName() + " has its own @UseEnvironment"))
				.orElseGet(() -> FilterResult.included("Belongs to the class this run owns"));
	}

}
