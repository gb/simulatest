package org.simulatest.environment;

import org.simulatest.environment.infra.exception.EnvironmentInstantiationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.InaccessibleObjectException;
import java.util.Objects;

/**
 * Default {@link EnvironmentFactory} that instantiates environments via their
 * declared no-arg constructor (made accessible if needed). Reflection failures
 * are wrapped in {@link EnvironmentInstantiationException}, including the
 * unchecked failures {@code setAccessible} can raise on Java 9+ modules
 * ({@link InaccessibleObjectException}, {@link SecurityException}).
 *
 * <p>DI plugins can replace this factory with one that resolves instances from
 * a container; see {@link org.simulatest.environment.plugin.SimulatestPlugin#environmentFactory()}.</p>
 */
public final class EnvironmentReflectionFactory implements EnvironmentFactory {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentReflectionFactory.class);

	@Override
	public Environment create(EnvironmentDefinition definition) {
		Objects.requireNonNull(definition, "definition must not be null");
		try {
			logger.trace("Instantiating environment: {}", definition.getName());
			Constructor<? extends Environment> constructor = definition.getEnvironmentClass().getDeclaredConstructor();
			constructor.setAccessible(true);
			return constructor.newInstance();
		} catch (ReflectiveOperationException | InaccessibleObjectException | SecurityException exception) {
			String message = "Error in instantiation of environment: " + definition.getName();
			throw new EnvironmentInstantiationException(message, exception);
		}
	}

}