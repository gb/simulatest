package objectiveLabs.environmentTestHarness.environment;


public class EnvironmentDefinition {

	private final Class<? extends Environment> environmentClass;
	
	public static EnvironmentDefinition create(Class<? extends Environment> environmentClass) {
		if (environmentClass == null) return null;
		return new EnvironmentDefinition(environmentClass);
	}

	public static EnvironmentDefinition bigBang() {
		return create(BigBangEnvironment.class);
	}
	
	private EnvironmentDefinition(Class<? extends Environment> environmentClass) {
		this.environmentClass = environmentClass;
	}
	
	public Class<? extends Environment> getEnvironmentClass() {
		return environmentClass;
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