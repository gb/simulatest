package org.simulatest.environment.bootstrap;

import org.simulatest.environment.plugin.SimulatestPlugin;

/**
 * Marker plugins registered via the test-resources {@code META-INF/services}
 * file so {@link DatabaseBootstrapPluginOrderingTest} can verify the
 * bootstrap plugin lands last with multiple non-bootstrap plugins present.
 * Two stand-ins are needed: with only one, a buggy comparator returning 0
 * for everything could pass by accident if ServiceLoader happened to
 * return bootstrap last on its own.
 */
final class FakePluginsForOrderingTest {

	private FakePluginsForOrderingTest() {}

	public static final class FirstFake implements SimulatestPlugin {}

	public static final class SecondFake implements SimulatestPlugin {}

}
