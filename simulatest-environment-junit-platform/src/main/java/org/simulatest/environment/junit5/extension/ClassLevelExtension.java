package org.simulatest.environment.junit5.extension;

import java.util.Optional;

import org.junit.jupiter.api.extension.AfterAllCallback;
import org.junit.jupiter.api.extension.BeforeAllCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ExtensionContext.Namespace;
import org.simulatest.environment.SetupLevel;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * Jupiter extension that gives each test class one Insistence Layer level
 * of its own, so the data its class setup writes ({@code @BeforeAll}, a
 * per-class test instance) is seen by every test of the class.
 *
 * <p>The level opens right before the first test of the class, after the
 * class setup ran. The reset after each test goes back to it, so the class
 * setup data stays and the data of the test is undone. When the class ends,
 * one rollback undoes the level and the class setup data below it, before
 * the next class starts.</p>
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
public final class ClassLevelExtension implements BeforeAllCallback, BeforeEachCallback, AfterAllCallback {

	private static final Namespace NAMESPACE = Namespace.create(ClassLevelExtension.class);

	@Override
	public void beforeAll(ExtensionContext classContext) {
		SimulatestExecutionContext simulatest = SimulatestExecutionContext.getCurrent();
		if (simulatest == null) return;

		levelOfOuterClass(classContext).ifPresent(SetupLevel::open);
		classContext.getStore(NAMESPACE).put(classContext.getRequiredTestClass(), simulatest.newSetupLevel());
	}

	@Override
	public void beforeEach(ExtensionContext testContext) {
		levelOf(testContext.getRequiredTestClass(), testContext).ifPresent(SetupLevel::open);
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
