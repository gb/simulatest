package org.simulatest.environment;

import java.util.Objects;

import org.simulatest.environment.annotation.EnvironmentParent;

public class EnvironmentDefinition {

	private final Class<? extends Environment> environmentClass;
	private final Class<? extends Environment> parentClass;

	public static EnvironmentDefinition create(Class<? extends Environment> environmentClass) {
		Objects.requireNonNull(environmentClass, "Null Argument! Don't you want create a Definition?");
		return new EnvironmentDefinition(environmentClass);
	}

	private static final EnvironmentDefinition BIG_BANG = create(BigBangEnvironment.class);

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
	   if (obj == null) return false;
	   if (obj == this) return true; 
	   if (obj.getClass() != getClass()) return false;
	   
	   return ((EnvironmentDefinition) obj).environmentClass == environmentClass;
	}

}