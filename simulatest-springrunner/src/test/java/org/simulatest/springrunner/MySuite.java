package org.simulatest.springrunner;


import org.junit.runner.RunWith;
import org.junit.runners.Suite.SuiteClasses;
import org.simulatest.springrunner.junit.EnvironmentSpringSuiteRunner;

@RunWith(EnvironmentSpringSuiteRunner.class)
@SuiteClasses(value = { SimpleSpringTest.class, AnotherSimpleSpringTest.class })
public class MySuite {

}
