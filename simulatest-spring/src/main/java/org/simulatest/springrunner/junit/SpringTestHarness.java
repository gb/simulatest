package org.simulatest.springrunner.junit;

import org.junit.runner.RunWith;
import org.simulatest.springrunner.spring.SimulatestSpringConfig;

@RunWith(SimulatestSpringRunner.class)
@SimulatestSpringConfig(locations = "simulatest-applicationContext.xml")
public class SpringTestHarness extends AbstractSpringTestHarness {

}