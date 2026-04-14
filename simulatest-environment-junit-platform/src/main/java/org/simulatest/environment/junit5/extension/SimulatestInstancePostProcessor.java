package org.simulatest.environment.junit5.extension;

import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.TestInstancePostProcessor;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Jupiter extension that bridges Simulatest's plugin-based post-processing
 * into Jupiter's test instance lifecycle.
 *
 * <p>After Jupiter creates a test instance, this extension asks the current
 * Simulatest session to run plugin post-processing (DI injection, etc.).
 * Guarded by a ThreadLocal — no-op outside Simulatest.</p>
 */
public final class SimulatestInstancePostProcessor implements TestInstancePostProcessor {

	@Override
	public void postProcessTestInstance(Object testInstance, ExtensionContext context) {
		SimulatestExecutionContext ctx = SimulatestExecutionContext.getCurrent();
		if (ctx != null) ctx.postProcessTestInstance(testInstance);
	}

}
