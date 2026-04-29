package org.simulatest.di.guice;

import java.util.List;

import com.google.inject.Module;

/**
 * SPI for supplying Guice modules dynamically. Reference an implementation
 * from {@link SimulatestGuiceConfig#providers()} on a test class to plug it
 * into the test's injector.
 *
 * <p>Implementations must expose a public no-arg constructor; they are
 * instantiated once per test session, on the single thread that initializes
 * the {@link GuiceContext}.</p>
 */
public interface GuiceModuleProvider {

	/**
	 * Returns the modules to add to the test injector. Called once during
	 * context initialization. Must not return {@code null}.
	 */
	List<Module> modules();

}
