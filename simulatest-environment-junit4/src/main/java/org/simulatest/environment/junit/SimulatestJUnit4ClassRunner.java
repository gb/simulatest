package org.simulatest.environment.junit;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.simulatest.environment.plugin.SimulatestPlugin;
import org.simulatest.environment.SetupLevel;
import org.simulatest.environment.SimulatestSession;
import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.simulatest.insistencelayer.InsistenceLayer;

/**
 * Runs one test class inside the Insistence Layer level of its environment.
 *
 * <p>The class gets one level of its own, opened after its class setup
 * ({@code @BeforeClass}, class rules) and before its first test. The reset
 * after each test goes back to it, so the class setup data stays for every
 * test and the data of each test is undone. When the class ends, one
 * rollback undoes the level and the class setup data below it, before the
 * next class starts.</p>
 *
 * <p>The cost is one savepoint and one rollback per class, however many
 * tests it has.</p>
 */
public final class SimulatestJUnit4ClassRunner extends BlockJUnit4ClassRunner {

	private final Supplier<Optional<InsistenceLayer>> insistenceLayer;
	private final List<SimulatestPlugin> plugins;

	// Set when the class starts and opened when its tests start. JUnit runs the
	// tests (childrenInvoker) inside the class (classBlock), so it is set first.
	private SetupLevel classLevel = SetupLevel.withoutLayer();

	/**
	 * @param insistenceLayer answers with the layer of the running suite; asked
	 *                        each time, because the layer is only known once the suite runs
	 */
	public SimulatestJUnit4ClassRunner(Supplier<Optional<InsistenceLayer>> insistenceLayer, Class<?> clazz,
			List<SimulatestPlugin> plugins) throws InitializationError {
		super(clazz);
		this.insistenceLayer = Objects.requireNonNull(insistenceLayer, "insistenceLayer must not be null");
		this.plugins = Objects.requireNonNull(plugins, "plugins must not be null");
	}

	@Override
	protected Object createTest() throws Exception {
		Object instance = SimulatestSession.createTestInstanceOrElse(plugins, getTestClass().getJavaClass(), this::newTestInstance);
		SimulatestSession.postProcessWithPlugins(plugins, instance);
		return instance;
	}

	private Object newTestInstance() {
		try {
			return super.createTest();
		} catch (Exception e) {
			throw new EnvironmentInstantiationException("Failed to create test instance: " + getTestClass().getName(), e);
		}
	}

	// The class ends here, after its teardown. Undoing the class setup with
	// the level lets the next class start from the environment alone, also
	// when the class setup failed and the level never opened.
	@Override
	protected Statement classBlock(RunNotifier notifier) {
		Statement wholeClass = super.classBlock(notifier);
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				classLevel = newClassLevel();
				try {
					wholeClass.evaluate();
				} finally {
					classLevel.closeUndoingSetup();
				}
			}
		};
	}

	// JUnit runs the tests after the class setup, so the level opens on top of
	// the class setup data.
	@Override
	protected Statement childrenInvoker(RunNotifier notifier) {
		Statement tests = super.childrenInvoker(notifier);
		return new Statement() {
			@Override
			public void evaluate() throws Throwable {
				classLevel.open();
				tests.evaluate();
			}
		};
	}

	private SetupLevel newClassLevel() {
		return SetupLevel.onLayerIfAny(insistenceLayer.get());
	}

	// Reset the level of the class after each test so sibling tests start
	// from the same state: the environment and the class setup.
	@Override
	protected void runChild(FrameworkMethod method, RunNotifier notifier) {
		try {
			super.runChild(method, notifier);
		} finally {
			classLevel.reset();
		}
	}

}
