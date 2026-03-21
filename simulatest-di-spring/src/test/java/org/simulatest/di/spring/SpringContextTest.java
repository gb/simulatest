package org.simulatest.di.spring;

import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.util.List;

import org.junit.jupiter.api.Test;

class SpringContextTest {

	@Test
	void shouldDiscoverConfigurationViaComponentScanning() {
		SpringContext context = new SpringContext();
		context.initialize(List.of(SimpleSpringJUnit5Sample.class));

		assertNotNull(context.getInstance(SimpleSpringJUnit5Sample.Greeter.class));

		context.destroy();
	}

}
