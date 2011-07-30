package org.simulatest.environment.environment;

import static org.simulatest.environment.infra.AnnotationUtils.extractEnvironmentParent;

public class EnvironmentDefinition {

	private final Class<? extends Environment> environmentClass;
	private final Class<? extends Environment> parentClass;
	
	public static EnvironmentDefinition create(Class<? extends Environment> environmentClass) {
		if (environmentClass == null) return null;
		return new EnvironmentDefinition(environmentClass);
	}

	public static EnvironmentDefinition bigBang() {
		return create(BigBangEnvironment.class);
	}
	
	private EnvironmentDefinition(Class<? extends Environment> environmentClass) {
		this.environmentClass = environmentClass;
		this.parentClass = extractEnvironmentParent(environmentClass);
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