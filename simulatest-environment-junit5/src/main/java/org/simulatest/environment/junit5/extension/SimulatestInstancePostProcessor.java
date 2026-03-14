package org.simulatest.environment.junit5.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.simulatest.environment.environment.SimulatestPlugins;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Jupiter extension that bridges Simulatest's plugin-based post-processing
 * into Jupiter's test instance lifecycle.
 *
 * <p>After Jupiter creates a test instance, this extension calls
 * {@link SimulatestPlugins#postProcessAll} so that DI plugins (Spring, Guice, etc.)
 * can inject dependencies. Guarded by a ThreadLocal — no-op outside Simulatest.</p>
 */
public class SimulatestInstancePostProcessor implements TestInstancePostProcessor {

	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
		SimulatestExecutionContext ctx = SimulatestExecutionContext.getCurrent();
		if (ctx != null) {
			SimulatestPlugins.postProcessAll(ctx.plugins(), testInstance);
		}
	}

}
