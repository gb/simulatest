package org.simulatest.springrunner.junit;

import org.simulatest.springrunner.spring.SimulatestContextLoader;
import org.springframework.test.context.ContextConfiguration;

@ContextConfiguration (
		locations = { "/simulatest-applicationContext.xml" }, 
		loader = SimulatestContextLoader.class
)
abstract class AbstractSpringTestHarness {

}