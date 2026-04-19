package org.simulatest.environment;

import java.util.Objects;

import org.simulatest.environment.annotation.EnvironmentParent;

/**
 * Immutable descriptor for an {@link Environment}, pairing the environment class
 * with the parent declared by its {@link EnvironmentParent} annotation (or
 * {@link BigBangEnvironment} when none is declared).
 *
 * <p>Two definitions are equal when they wrap the same environment class.
 * Used by {@link EnvironmentExtractor} and {@link org.simulatest.environment.tree.EnvironmentTreeBuilder}
 * to build the environment tree.</p>
 */
public final class EnvironmentDefinition {

	private final Class<? extends Environment> environmentClass;
	private final Class<? extends Environment> parentClass;

	/**
	 * Creates a definition for the given environment class.
	 *
	 * @param environmentClass the environment class; must be non-null
	 * @return a new definition
	 */
	public static EnvironmentDefinition create(Class<? extends Environment> environmentClass) {
		Objects.requireNonNull(environmentClass, "environmentClass must not be null");
		return new EnvironmentDefinition(environmentClass);
	}

	private static final EnvironmentDefinition BIG_BANG = create(BigBangEnvironment.class);

	/**
	 * Returns the shared definition for {@link BigBangEnvironment}, the implicit
	 * root of every environment tree.
	 */
	public static EnvironmentDefinition bigBang() {
		return BIG_BANG;
	}

	private EnvironmentDefinition(Class<? extends Environment> environmentClass) {
		this.environmentClass = environmentClass;
		EnvironmentParent annotation = environmentClass.getAnnotation(EnvironmentParent.class);
		this.parentClass = annotation == null ? BigBangEnvironment.class : annotation.value();
	}
	
	public Class<? extends Environment> getEnvironmentClass() {
		return environmentClass;
	}
	
	public Class<? extends Environment> getParentClass() {
		return parentClass;
	}

	public EnvironmentDefinition createParentDefinition() {
		return create(parentClass);
	}

	public String getName() {
		return environmentClass.getSimpleName();
	}

	@Override
	public String toString() {
		return getName();
	}

	@Override
	public int hashCode() {
		return environmentClass.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		return obj instanceof EnvironmentDefinition that
				&& this.environmentClass == that.environmentClass;
	}

}