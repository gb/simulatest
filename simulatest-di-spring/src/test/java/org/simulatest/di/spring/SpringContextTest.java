package org.simulatest.di.spring;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class SpringContextTest {

	@Test
	void shouldDiscoverConfigurationViaComponentScanning() {
		SpringContext context = new SpringContext();
		context.initialize(List.of(SimpleSpringJUnit5IT.class));

		assertNotNull(context.getInstance(SimpleSpringJUnit5IT.Greeter.class));

		context.destroy();
	}

}
