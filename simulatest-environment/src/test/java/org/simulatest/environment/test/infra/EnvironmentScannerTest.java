package org.simulatest.environment.test.infra;

import static org.junit.Assert.assertEquals;

import java.util.Collection;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import org.simulatest.environment.environment.BigBangEnvironment;
import org.simulatest.environment.infra.EnvironmentScanner;

public class EnvironmentScannerTest {
	
	private EnvironmentScanner environmentScanner;
	
	@Before
	public void setup() {
		environmentScanner = new EnvironmentScanner("org.simulatest.environment.environment");
	}
	
	@Test
	public void testScanBasePackageLookingForEnvironments() {
		Collection<Class<?>> expected = new HashSet<Class<?>>();
		expected.add(BigBangEnvironment.class);
		
		assertEquals(expected, environmentScanner.getEnvironmentList());
	}

}