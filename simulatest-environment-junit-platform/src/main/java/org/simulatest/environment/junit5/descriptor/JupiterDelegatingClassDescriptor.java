package org.simulatest.environment.junit5.descriptor;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.platform.engine.DiscoverySelector;
import org.junit.platform.engine.UniqueId;
import org.junit.platform.engine.support.descriptor.AbstractTestDescriptor;
import org.junit.platform.engine.support.descriptor.ClassSource;
import org.junit.platform.engine.support.hierarchical.Node;
import org.junit.platform.launcher.TestIdentifier;
import org.simulatest.environment.junit5.SimulatestExecutionContext;

/**
 * One test class, whose tests are discovered and run by Jupiter.
 *
 * <p>At discovery the tests Jupiter finds become children of this descriptor
 * ({@link JupiterTestDescriptor}), so the launcher sees them before the run
 * and its filters can take some out. At execution Jupiter runs the children
 * that are left, in one inner run, and {@link JupiterRunReporter} reports
 * each of them at the moment Jupiter does.</p>
 *
 * <p>The level of the class in the Insistence Layer is handled by
 * {@link org.simulatest.environment.junit5.extension.ClassLevelExtension},
 * which Jupiter auto-detects during the inner run.</p>
 *
 * <p><b>Thread-safety:</b> not thread-safe; one descriptor instance is executed
 * by the JUnit Platform on a single executor thread per parent.</p>
 */
public final class JupiterDelegatingClassDescriptor extends AbstractTestDescriptor
		implements Node<SimulatestExecutionContext> {

	/** Type of the unique id segment that names a test class. */
	public static final String CLASS_SEGMENT = "class";

	/**
	 * Type of the unique id segment that names one test. Its value is the
	 * unique id Jupiter gave the test, so a later run can select the same test.
	 */
	public static final String TEST_SEGMENT = "test";

	private final Class<?> testClass;
	private final JupiterClassLauncher jupiter;
	private List<JupiterTestDescriptor> testsToRun = List.of();
	private RuntimeException discoveryFailure;

	private JupiterDelegatingClassDescriptor(UniqueId uniqueId, Class<?> testClass, List<DiscoverySelector> parts) {
		super(uniqueId, testClass.getSimpleName(), ClassSource.from(testClass));
		this.testClass = testClass;
		this.jupiter = new JupiterClassLauncher(testClass, parts);
	}

	/**
	 * Creates the descriptor of {@code testClass} with one child for each test
	 * Jupiter finds in it.
	 *
	 * @param parts the selectors that narrow the run to part of {@code testClass};
	 *              empty to run the whole class
	 */
	public static JupiterDelegatingClassDescriptor discover(UniqueId uniqueId, Class<?> testClass,
			List<DiscoverySelector> parts) {
		Objects.requireNonNull(testClass, "testClass must not be null");
		Objects.requireNonNull(parts, "parts must not be null");

		JupiterDelegatingClassDescriptor descriptor = new JupiterDelegatingClassDescriptor(uniqueId, testClass, parts);
		descriptor.discoverTests();
		return descriptor;
	}

	// A class Jupiter cannot discover must not stop the discovery of the others.
	// Its failure is kept and reported when the class runs.
	private void discoverTests() {
		try {
			for (TestIdentifier discovered : jupiter.discover()) addChild(new JupiterTestDescriptor(this, discovered));
		} catch (RuntimeException failure) {
			discoveryFailure = failure;
		}
	}

	public Class<?> testClass() {
		return testClass;
	}

	@Override
	public Type getType() {
		return Type.CONTAINER;
	}

	// The launcher drops a container with no test in it. A class that could not
	// be discovered has no child, and has to stay to report why.
	@Override
	public boolean mayRegisterTests() {
		return discoveryFailure != null;
	}

	@Override
	public SimulatestExecutionContext prepare(SimulatestExecutionContext context) {
		testsToRun = takeTestsOutOfTheTree();
		return context;
	}

	// The platform would run the children itself after the class: every test
	// reported a second time, and only when the class is over. The launcher still
	// knows them from discovery. The ones left here are the ones no filter of
	// the launcher took out.
	private List<JupiterTestDescriptor> takeTestsOutOfTheTree() {
		List<JupiterTestDescriptor> testsLeft = getChildren().stream()
				.map(JupiterTestDescriptor.class::cast)
				.toList();
		testsLeft.forEach(this::removeChild);
		return testsLeft;
	}

	@Override
	public SimulatestExecutionContext execute(SimulatestExecutionContext context,
			DynamicTestExecutor dynamicTestExecutor) {
		if (discoveryFailure != null) throw discoveryFailure;

		JupiterRunReporter reporter = new JupiterRunReporter(this, testsToRun, context.listener());
		SimulatestExecutionContext.withCurrent(context, () -> jupiter.run(jupiterIdsOfTestsToRun(), reporter));
		reporter.rethrowFailuresOfContainers();
		return context;
	}

	private Set<UniqueId> jupiterIdsOfTestsToRun() {
		return testsToRun.stream()
				.map(JupiterTestDescriptor::jupiterId)
				.collect(Collectors.toUnmodifiableSet());
	}

}
