package org.simulatest.environment.junit5.extension;

import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Jupiter extension that resets the Insistence Layer after each test,
 * ensuring sibling tests start with the same database state.
 *
 * <p>Auto-registered via {@code META-INF/services} when the Simulatest engine
 * delegates class execution to Jupiter. Guarded by a ThreadLocal — acts as a
 * no-op when running outside Simulatest.</p>
 */
public class InsistenceAfterEachExtension implements AfterEachCallback {

	@Override
	public void afterEach(ExtensionContext context) {
		SimulatestExecutionContext ctx = SimulatestExecutionContext.getCurrent();
		if (ctx != null && ctx.insistenceLayer() != null) {
			ctx.insistenceLayer().resetCurrentLevel();
		}
	}

}
