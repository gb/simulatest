package org.simulatest.di.jee;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.se.SeContainer;
import jakarta.enterprise.inject.se.SeContainerInitializer;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionTarget;

import org.simulatest.environment.plugin.DependencyInjectionContext;

/**
 * <p><b>Thread-safety:</b> not thread-safe. Initialize and destroy from the
 * owning test thread; the underlying CDI container is thread-safe for reads.</p>
 */
public final class CdiContext implements DependencyInjectionContext {

	private SeContainer container;
	private final Map<Class<?>, InjectionTarget<Object>> injectionTargetCache = new HashMap<>();

	@Override
	public <T> T getInstance(Class<T> clazz) {
		return getContainer().select(clazz).get();
	}

	@Override
	public void injectMembers(Object instance) {
		InjectionTarget<Object> injectionTarget = injectionTargetCache.computeIfAbsent(
			instance.getClass(), this::createInjectionTarget);
		CreationalContext<Object> creationalContext = getContainer().getBeanManager().createCreationalContext(null);
		try {
			injectionTarget.inject(instance, creationalContext);
		} finally {
			creationalContext.release();
		}
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		if (container != null && container.isRunning()) return;
		container = SeContainerInitializer.newInstance().initialize();
	}

	@Override
	public Optional<DataSource> dataSource() {
		var instance = getContainer().select(DataSource.class);
		return instance.isResolvable() ? Optional.of(instance.get()) : Optional.empty();
	}

	@Override
	public void destroy() {
		if (container != null && container.isRunning()) {
			try {
				container.close();
			} finally {
				injectionTargetCache.clear();
				container = null;
			}
		}
	}

	@SuppressWarnings("unchecked")
	private InjectionTarget<Object> createInjectionTarget(Class<?> clazz) {
		BeanManager beanManager = getContainer().getBeanManager();
		AnnotatedType<Object> type = (AnnotatedType<Object>) beanManager.createAnnotatedType(clazz);
		return beanManager.getInjectionTargetFactory(type).createInjectionTarget(null);
	}

	private SeContainer getContainer() {
		if (container == null || !container.isRunning()) {
			throw new IllegalStateException("CDI container is not running. "
				+ "Add simulatest-di-jee to the classpath.");
		}
		return container;
	}

}
