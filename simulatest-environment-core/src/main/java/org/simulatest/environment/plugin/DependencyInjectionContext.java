package org.simulatest.environment.plugin;

import java.lang.annotation.Annotation;
import java.util.Collection;
import java.util.Optional;

import javax.sql.DataSource;

/**
 * Abstraction over a DI container (Spring, Guice, CDI, etc.).
 *
 * <p>Each DI module implements this interface once. The generic
 * {@link DependencyInjectionPlugin} handles the rest -- no need for
 * per-module plugin or factory subclasses.</p>
 */
public interface DependencyInjectionContext {

	/**
	 * Returns an instance of {@code clazz} from the container, creating and
	 * autowiring one if no managed instance exists.
	 *
	 * @param <T> the requested type
	 * @param clazz the class to resolve
	 * @return a managed or freshly autowired instance
	 */
	<T> T getInstance(Class<T> clazz);

	/**
	 * Injects container-managed dependencies into an already-constructed
	 * instance (e.g., a test instance built by JUnit).
	 *
	 * @param instance the target object
	 */
	void injectMembers(Object instance);

	/**
	 * Boots the container. Called once per test suite, before any environment
	 * runs.
	 *
	 * @param testClasses every test class that will run in the suite
	 */
	void initialize(Collection<Class<?>> testClasses);

	/** Tears the container down. Called once after the test suite finishes. */
	void destroy();

	/**
	 * Returns the {@link DataSource} the container exposes, if any. When
	 * present, the runner uses it to drive Insistence Layer savepoints.
	 */
	Optional<DataSource> dataSource();

	/**
	 * Finds the first occurrence of {@code annotationType} on any of
	 * {@code testClasses}. Useful for discovering DI configuration annotations
	 * (e.g., a Spring {@code @ContextConfiguration}) declared anywhere in the
	 * suite.
	 *
	 * @param <A> the annotation type
	 * @param testClasses test classes to scan
	 * @param annotationType the annotation class to look for
	 * @return the first matching annotation, or empty if none is found
	 */
	static <A extends Annotation> Optional<A> findConfigAnnotation(Collection<Class<?>> testClasses, Class<A> annotationType) {
		return testClasses.stream()
				.filter(clazz -> clazz.isAnnotationPresent(annotationType))
				.findFirst()
				.map(clazz -> clazz.getAnnotation(annotationType));
	}

}
