package org.simulatest.springrunner.junit;

import org.junit.runner.RunWith;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;

@RunWith(SimulatestSpringSuiteRunner.class)
@SimulatestSpringConfig(locations = "simulatest-applicationContext.xml")
public class SpringSuiteTestHarness extends AbstractSpringTestHarness {

}