package org.simulatest.environment.junit5.extension;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.junit.jupiter.api.extension.TestInstanceFactoryContext;
import org.junit.jupiter.api.extension.TestInstancePreConstructCallback;
import org.simulatest.environment.SetupLevel;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Jupiter extension that gives each test class one Insistence Layer level
 * of its own, so the data its class setup writes ({@code @BeforeAll}, a
 * per-class test instance) is seen by every test of the class.
 *
 * <p>The level opens after the class setup ran and before the first test,
 * or before the instance of the first test is built when each test gets its
 * own instance. So what a test instance writes when it is built belongs to
 * its test. The level is reset after each test, so sibling tests start from
 * the same state: the class setup data stays and the data of the test is
 * undone. When the class ends, one rollback undoes the level and the class
 * setup data below it, before the next class starts.</p>
 *
 * <p>The cost is one savepoint and one rollback per class, however many
 * tests it has. The environments are not run again and their levels are not
 * touched.</p>
 *
 * <p>A {@code @Nested} class gets its own level on top of the level of its
 * outer class. The level of the outer class opens before the class setup of
 * the nested class runs, also when the outer class has no test of its own.</p>
 *
 * <p>Auto-registered via {@code META-INF/services}. Does nothing outside a
 * Simulatest run.</p>
 */
public final class ClassLevelExtension implements BeforeAllCallback, TestInstancePreConstructCallback,
		BeforeEachCallback, AfterEachCallback, AfterAllCallback {

	private static final Namespace NAMESPACE = Namespace.create(ClassLevelExtension.class);

	@Override
	public void beforeAll(ExtensionContext classContext) {
		SimulatestExecutionContext simulatest = SimulatestExecutionContext.getCurrent();
		if (simulatest == null) return;

		levelOfOuterClass(classContext).ifPresent(SetupLevel::open);
		classContext.getStore(NAMESPACE).put(classContext.getRequiredTestClass(), simulatest.newSetupLevel());
	}

	// With one instance per test, the instance is built after the class setup,
	// and the level of the class is already known here. With one instance per
	// class, the instance is built before the class setup; the level is not
	// known yet, and beforeEach opens it.
	@Override
	public void preConstructTestInstance(TestInstanceFactoryContext factoryContext, ExtensionContext context) {
		levelOf(context.getRequiredTestClass(), context).ifPresent(SetupLevel::open);
	}

	@Override
	public void beforeEach(ExtensionContext testContext) {
		levelOf(testContext.getRequiredTestClass(), testContext).ifPresent(SetupLevel::open);
	}

	@Override
	public void afterEach(ExtensionContext testContext) {
		levelOf(testContext.getRequiredTestClass(), testContext).ifPresent(SetupLevel::reset);
	}

	@Override
	public void afterAll(ExtensionContext classContext) {
		levelOf(classContext.getRequiredTestClass(), classContext).ifPresent(SetupLevel::closeUndoingSetup);
	}

	private static Optional<SetupLevel> levelOfOuterClass(ExtensionContext classContext) {
		return classContext.getParent()
				.flatMap(ExtensionContext::getTestClass)
				.flatMap(outerClass -> levelOf(outerClass, classContext));
	}

	// A store also answers with the values of the contexts around it. That is how
	// a test finds the level of its class, and a nested class the level of its
	// outer class. The class is the key, so one class never gets the level of another.
	private static Optional<SetupLevel> levelOf(Class<?> testClass, ExtensionContext context) {
		return Optional.ofNullable(context.getStore(NAMESPACE).get(testClass, SetupLevel.class));
	}

}
