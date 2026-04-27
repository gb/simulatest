package org.simulatest.environment.infra;

import java.util.List;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.stream.Collectors;

/**
 * Wrappers around {@link ServiceLoader} for the discovery patterns the project uses.
 *
 * <p>Beyond plain {@link #loadAll}, the {@link #loadAtMostOne} and
 * {@link #loadExactlyOne} helpers centralize the "fail listing all FQNs"
 * error message so every SPI in the project reports authorship mistakes
 * in the same shape.</p>
 */
public final class ServiceLoaders {

	private ServiceLoaders() {}

	public static <T> List<T> loadAll(Class<T> service) {
		return ServiceLoader.load(service).stream()
				.map(ServiceLoader.Provider::get)
				.toList();
	}

	/**
	 * Returns the single registered provider of {@code service}, or
	 * {@link Optional#empty()} when none is registered. Throws if more
	 * than one is registered, naming all of them.
	 */
	public static <T> Optional<T> loadAtMostOne(Class<T> service) {
		return atMostOne(service, loadAll(service));
	}

	/**
	 * Returns the single registered provider of {@code service}. Throws
	 * if zero or more than one is registered, with a message that names
	 * the {@code META-INF/services} file the user should populate or trim.
	 */
	public static <T> T loadExactlyOne(Class<T> service) {
		return exactlyOne(service, loadAll(service));
	}

	static <T> Optional<T> atMostOne(Class<T> service, List<T> all) {
		if (all.size() > 1) throw multipleImplementations(service, all);
		return all.isEmpty() ? Optional.empty() : Optional.of(all.get(0));
	}

	static <T> T exactlyOne(Class<T> service, List<T> all) {
		if (all.isEmpty()) {
			throw new IllegalStateException(
					"No " + service.getSimpleName() + " was found on the classpath. "
							+ "Register one via META-INF/services/" + service.getName() + ".");
		}
		if (all.size() > 1) throw multipleImplementations(service, all);
		return all.get(0);
	}

	private static <T> IllegalStateException multipleImplementations(Class<T> service, List<T> all) {
		String names = all.stream()
				.map(impl -> impl.getClass().getName())
				.collect(Collectors.joining(", "));
		return new IllegalStateException(
				"Multiple " + service.getSimpleName() + " implementations found on the classpath: "
						+ names + ". Keep exactly one.");
	}

}
