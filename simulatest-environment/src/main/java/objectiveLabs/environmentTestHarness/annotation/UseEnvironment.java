package objectiveLabs.environmentTestHarness.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import objectiveLabs.environmentTestHarness.environment.Environment;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface UseEnvironment {

	public Class<? extends Environment> value();
	
}