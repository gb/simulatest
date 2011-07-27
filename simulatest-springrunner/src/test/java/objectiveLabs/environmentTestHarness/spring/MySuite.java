package objectiveLabs.environmentTestHarness.spring;

import objectiveLabs.environmentTestHarness.junit.EnvironmentSpringSuiteRunner;

import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;

@RunWith(EnvironmentSpringSuiteRunner.class)
@SuiteClasses(value = { SimpleSpringTest.class, AnotherSimpleSpringTest.class })
public class MySuite {

}
