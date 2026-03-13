package org.simulatest.environment.environment;

import java.util.Collection;

/**
 * Generic {@link SimulatestPlugin} that delegates everything to a
 * {@link DependencyInjectionContext}. DI modules subclass this with
 * a one-liner constructor — no per-module factory class needed.
 */
public class DependencyInjectionPlugin implements SimulatestPlugin {

	private final DependencyInjectionContext context;

	protected DependencyInjectionPlugin(DependencyInjectionContext context) {
		this.context = context;
	}

	@Override
	public final EnvironmentFactory environmentFactory() {
		return definition -> context.getInstance(definition.getEnvironmentClass());
	}

	@Override
	public final void initialize(Collection<Class<?>> testClasses) {
		context.initialize(testClasses);
	}

	@Override
	public final void destroy() {
		context.destroy();
	}

	@Override
	public Object createTestInstance(Class<?> testClass) {
		try {
			return context.getInstance(testClass);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public final void postProcessTestInstance(Object instance) {
		context.injectMembers(instance);
	}

}
