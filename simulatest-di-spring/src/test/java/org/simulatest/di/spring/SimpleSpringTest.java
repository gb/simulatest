package org.simulatest.di.spring;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.simulatest.environment.annotation.UseEnvironment;
import org.simulatest.environment.junit.EnvironmentJUnitRunner;

import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.Assert.assertEquals;

@RunWith(EnvironmentJUnitRunner.class)
@UseEnvironment(SimpleSpringJUnit5Sample.ChildEnv.class)
public class SimpleSpringTest {

	@Autowired private SimpleSpringJUnit5Sample.Greeter greeter;
	@Autowired private SimpleSpringJUnit5Sample.MessageLog log;

	@Test
	public void simpleSpringDITest() {
		assertEquals("Hello", greeter.greet());
	}

	@Test
	public void environmentsTest() {
		assertEquals(List.of("Hello", "Hello by child"), log.all());
	}

}
