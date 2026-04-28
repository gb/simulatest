package org.simulatest.di.quarkus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.sql.DataSource;

import io.quarkus.arc.Arc;
import io.quarkus.arc.ArcContainer;
import io.quarkus.arc.InstanceHandle;
import jakarta.enterprise.context.spi.CreationalContext;
import jakarta.enterprise.inject.spi.AnnotatedType;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.InjectionTarget;

import org.simulatest.environment.plugin.DependencyInjectionContext;
import org.simulatest.insistencelayer.InsistenceLayerFactory;

/**
 * {@link DependencyInjectionContext} backed by Quarkus's Arc container.
 *
 * <p>Unlike the Spring, Guice, and Jakarta CDI contexts, this one does not
 * own its container. Quarkus boots Arc inside {@code @QuarkusTest}'s
 * {@code beforeAll}; the {@link SimulatestQuarkusTestResource} configures
 * the Insistence Layer earlier, before Arc starts. By the time
 * {@link #getInstance(Class)} or {@link #injectMembers(Object)} is called,
 * Arc is up and {@link Arc#container()} is the source of truth.
 *
 * <p>{@link #initialize(Collection)} and {@link #destroy()} are no-ops:
 * Quarkus and the test resource handle bootstrap and teardown.
 *
 * <p><b>Thread-safety:</b> not thread-safe. Inherits the Insistence Layer's
 * single-thread constraint.
 */
public final class QuarkusContext implements DependencyInjectionContext {

	private final Map<Class<?>, InjectionTarget<Object>> injectionTargetCache = new HashMap<>();

	@Override
	public <T> T getInstance(Class<T> clazz) {
		try (InstanceHandle<T> handle = requireArc().instance(clazz)) {
			if (handle.isAvailable()) return handle.get();
		}
		throw new IllegalStateException("No Arc bean available for " + clazz.getName()
			+ ". Add @ApplicationScoped or @Dependent if its @Inject fields must populate.");
	}

	@Override
	public void injectMembers(Object instance) {
		ArcContainer arc = Arc.container();
		if (arc == null) return;

		BeanManager beanManager = arc.beanManager();
		InjectionTarget<Object> injectionTarget = injectionTargetCache.computeIfAbsent(
			instance.getClass(), c -> createInjectionTarget(beanManager, c));
		CreationalContext<Object> creationalContext = beanManager.createCreationalContext(null);
		try {
			injectionTarget.inject(instance, creationalContext);
		} finally {
			creationalContext.release();
		}
	}

	@Override
	public void initialize(Collection<Class<?>> testClasses) {
		// No-op. SimulatestQuarkusTestResource configures the Insistence Layer
		// before Quarkus boots Arc; this method runs in the outer classloader
		// before any of that happens, so it has nothing to do.
	}

	@Override
	public void destroy() {
		// No-op. Quarkus shuts Arc down on its own; SimulatestQuarkusTestResource
		// clears the Insistence Layer in stop().
		injectionTargetCache.clear();
	}

	@Override
	public Optional<DataSource> dataSource() {
		// Returned only when the test resource has already configured the
		// Insistence Layer. Before Arc boots, this is empty so
		// DependencyInjectionPlugin's autoConfigureInsistenceLayer is a no-op.
		return InsistenceLayerFactory.isConfigured()
			? Optional.of(InsistenceLayerFactory.requireDataSource())
			: Optional.empty();
	}

	private static ArcContainer requireArc() {
		ArcContainer arc = Arc.container();
		if (arc == null) {
			throw new IllegalStateException(
				"Arc container is not available. Ensure the test class is annotated with "
				+ "@QuarkusTest and that this resolution happens after Arc has booted.");
		}
		return arc;
	}

	@SuppressWarnings("unchecked")
	private static InjectionTarget<Object> createInjectionTarget(BeanManager beanManager, Class<?> clazz) {
		AnnotatedType<Object> type = (AnnotatedType<Object>) beanManager.createAnnotatedType(clazz);
		return beanManager.getInjectionTargetFactory(type).createInjectionTarget(null);
	}

}
