package org.simulatest.environment.infra;

import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

public class ServiceLoadersTest {

	@Test
	public void atMostOneShouldReturnEmptyForEmptyList() {
		assertTrue(ServiceLoaders.atMostOne(Marker.class, List.of()).isEmpty());
	}

	@Test
	public void atMostOneShouldReturnTheOnlyElement() {
		Marker only = new MarkerA();
		assertSame(only, ServiceLoaders.atMostOne(Marker.class, List.of(only)).orElseThrow());
	}

	@Test
	public void atMostOneShouldFailNamingAllImplementationsWhenMoreThanOne() {
		Marker first = new MarkerA();
		Marker second = new MarkerB();

		IllegalStateException error = assertThrows(IllegalStateException.class,
				() -> ServiceLoaders.atMostOne(Marker.class, List.of(first, second)));

		assertTrue(error.getMessage().contains(MarkerA.class.getName()));
		assertTrue(error.getMessage().contains(MarkerB.class.getName()));
		assertTrue(error.getMessage().contains("Keep exactly one"));
	}

	@Test
	public void exactlyOneShouldReturnTheOnlyElement() {
		Marker only = new MarkerA();
		assertSame(only, ServiceLoaders.exactlyOne(Marker.class, List.of(only)));
	}

	@Test
	public void exactlyOneShouldFailWithRegistrationGuidanceWhenEmpty() {
		IllegalStateException error = assertThrows(IllegalStateException.class,
				() -> ServiceLoaders.exactlyOne(Marker.class, List.of()));

		assertTrue(error.getMessage().contains("No " + Marker.class.getSimpleName()));
		assertTrue(error.getMessage().contains("META-INF/services/" + Marker.class.getName()));
	}

	@Test
	public void exactlyOneShouldFailNamingAllImplementationsWhenMoreThanOne() {
		IllegalStateException error = assertThrows(IllegalStateException.class,
				() -> ServiceLoaders.exactlyOne(Marker.class, List.of(new MarkerA(), new MarkerB())));

		assertTrue(error.getMessage().contains(MarkerA.class.getName()));
		assertTrue(error.getMessage().contains(MarkerB.class.getName()));
	}

	private interface Marker {}
	private static final class MarkerA implements Marker {}
	private static final class MarkerB implements Marker {}

}
