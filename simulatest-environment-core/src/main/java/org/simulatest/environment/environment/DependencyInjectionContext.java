package org.simulatest.environment.environment;

import java.util.Collection;

import javax.sql.DataSource;

/**
 * Abstraction over a DI container (Spring, Guice, CDI, etc.).
 *
 * <p>Each DI module implements this interface once. The generic
 * {@link DependencyInjectionPlugin} handles the rest — no need for
 * per-module plugin or factory subclasses.</p>
 */
public interface DependencyInjectionContext {

	<T> T getInstance(Class<T> clazz);

	void injectMembers(Object instance);

	void initialize(Collection<Class<?>> testClasses);

	void destroy();

	DataSource dataSource();

}
