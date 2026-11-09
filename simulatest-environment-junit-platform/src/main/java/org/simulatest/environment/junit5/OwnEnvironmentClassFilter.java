package org.simulatest.environment.junit5;

import java.util.Objects;
import java.util.Optional;

import org.junit.platform.engine.FilterResult;
import org.junit.platform.engine.TestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.launcher.PostDiscoveryFilter;
import org.simulatest.environment.annotation.UseEnvironment;

/**
 * Keeps an inner Jupiter run to the tests that belong to one
 * {@link UseEnvironment @UseEnvironment} class.
 *
 * <p>Jupiter discovers every {@code @Nested} class of the selected class. A
 * nested class with its own {@code @UseEnvironment} gets its own descriptor
 * from the Simulatest engine, so this filter drops its tests from the outer
 * class's run. Otherwise they would run twice, the first time under the
 * outer environment.</p>
 *
 * <p>Ownership follows the descriptor tree, not the lexical nesting: the
 * nearest ancestor class that declares {@code @UseEnvironment} owns the
 * test. A {@code @Nested} class inherited from an annotated base class is
 * therefore owned by the subclass being run.</p>
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
		return owningClass(descriptor)
				.filter(owner -> owner != environmentClass)
				.map(owner -> FilterResult.excluded(owner.getName() + " has its own @UseEnvironment"))
				.orElseGet(() -> FilterResult.included("Belongs to the class this run owns"));
	}

	/**
	 * Returns the class of the nearest descriptor, starting at {@code descriptor}
	 * itself and walking up its parents, that declares {@code @UseEnvironment}.
	 */
	private static Optional<Class<?>> owningClass(TestDescriptor descriptor) {
		for (TestDescriptor current = descriptor; current != null; current = current.getParent().orElse(null)) {
			Optional<Class<?>> declared = classDeclaringEnvironment(current);
			if (declared.isPresent()) return declared;
		}
		return Optional.empty();
	}

	private static Optional<Class<?>> classDeclaringEnvironment(TestDescriptor descriptor) {
		return descriptor.getSource()
				.filter(ClassSource.class::isInstance)
				.<Class<?>>map(source -> ((ClassSource) source).getJavaClass())
				.filter(clazz -> clazz.isAnnotationPresent(UseEnvironment.class));
	}

}
