package org.simulatest.di.jee;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import jakarta.enterprise.context.ApplicationScoped;
import jakarta.enterprise.context.Dependent;
import jakarta.inject.Inject;
import jakarta.inject.Named;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

class CdiContextInjectionTest {

	private static CdiContext context;

	@BeforeAll
	static void startContainer() {
		context = new CdiContext();
		context.initialize(List.of());
	}

	@AfterAll
	static void stopContainer() {
		context.destroy();
	}

	// -- beans --

	@ApplicationScoped
	public static class Greeter {
		public String greet() { return "Hello"; }
	}

	@ApplicationScoped @Named("english")
	public static class EnglishGreeter implements PolyglotGreeter {
		@Override public String greet() { return "Hello"; }
	}

	@ApplicationScoped @Named("french")
	public static class FrenchGreeter implements PolyglotGreeter {
		@Override public String greet() { return "Bonjour"; }
	}

	public interface PolyglotGreeter {
		String greet();
	}

	// -- test targets (not CDI beans themselves, just POJOs injected into) --

	@Dependent
	public static class MethodInjectionTarget {
		private Greeter greeter;

		@Inject
		public void setGreeter(Greeter greeter) {
			this.greeter = greeter;
		}
	}

	@Dependent
	public static class QualifierInjectionTarget {
		@Inject @Named("english") PolyglotGreeter english;
		@Inject @Named("french") PolyglotGreeter french;
	}

	@Dependent
	public static class BaseTarget {
		@Inject Greeter greeter;
	}

	@Dependent
	public static class DerivedTarget extends BaseTarget {
		@Inject @Named("french") PolyglotGreeter french;
	}

	// -- tests --

	@Test
	void methodInjection() {
		var target = new MethodInjectionTarget();
		context.injectMembers(target);
		assertNotNull(target.greeter, "setter-injected field should be populated");
		assertEquals("Hello", target.greeter.greet());
	}

	@Test
	void qualifierInjection() {
		var target = new QualifierInjectionTarget();
		context.injectMembers(target);
		assertEquals("Hello", target.english.greet());
		assertEquals("Bonjour", target.french.greet());
	}

	@Test
	void superclassFieldInjection() {
		var target = new DerivedTarget();
		context.injectMembers(target);
		assertNotNull(target.greeter, "superclass @Inject field should be populated");
		assertEquals("Hello", target.greeter.greet());
		assertEquals("Bonjour", target.french.greet());
	}

}
