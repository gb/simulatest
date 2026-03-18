package org.simulatest.environment.plugin;

import java.util.Collection;

import javax.sql.DataSource;

import org.simulatest.environment.EnvironmentFactory;
import org.simulatest.insistencelayer.InsistenceLayerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Generic {@link SimulatestPlugin} that delegates everything to a
 * {@link DependencyInjectionContext}. DI modules subclass this with
 * a one-liner constructor — no per-module factory class needed.
 */
public class DependencyInjectionPlugin implements SimulatestPlugin {

	private static final Logger logger = LoggerFactory.getLogger(DependencyInjectionPlugin.class);

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

		DataSource ds = context.dataSource();
		if (ds != null && !InsistenceLayerFactory.isConfigured()) {
			logger.info("Auto-configuring InsistenceLayer from DI context");
			InsistenceLayerFactory.configure(ds);
		}
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
			logger.debug("DI context could not create instance of {}, falling back to default construction",
					testClass.getName(), e);
			return null;
		}
	}

	@Override
	public final void postProcessTestInstance(Object instance) {
		context.injectMembers(instance);
	}

}
