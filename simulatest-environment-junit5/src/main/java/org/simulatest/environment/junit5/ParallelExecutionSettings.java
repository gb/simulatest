package org.simulatest.environment.junit5;

import java.util.LinkedHashMap;
import java.util.Map;

import org.junit.platform.engine.ConfigurationParameters;

/**
 * Resolves parallel execution settings from JUnit and Simulatest configuration parameters.
 */
public final class ParallelExecutionSettings {

	public static final String JUPITER_PARALLEL_ENABLED = "junit.jupiter.execution.parallel.enabled";
	public static final String JUPITER_MODE_DEFAULT = "junit.jupiter.execution.parallel.mode.default";
	public static final String JUPITER_MODE_CLASSES_DEFAULT = "junit.jupiter.execution.parallel.mode.classes.default";
	public static final String JUPITER_PARALLEL_CONFIG_PREFIX = "junit.jupiter.execution.parallel.config.";

	public static final String SIMULATEST_PARALLEL_ENABLED = "simulatest.execution.parallel.enabled";
	public static final String SIMULATEST_ALLOW_INSISTENCE_PARALLEL = "simulatest.execution.parallel.allow-insistence";

	private final boolean parallelEnabled;
	private final boolean allowParallelWhenInsistenceIsActive;
	private final Map<String, String> jupiterParameters;

	private ParallelExecutionSettings(boolean parallelEnabled,
			boolean allowParallelWhenInsistenceIsActive,
			Map<String, String> jupiterParameters) {
		this.parallelEnabled = parallelEnabled;
		this.allowParallelWhenInsistenceIsActive = allowParallelWhenInsistenceIsActive;
		this.jupiterParameters = Map.copyOf(jupiterParameters);
	}

	public static ParallelExecutionSettings from(ConfigurationParameters parameters) {
		boolean parallelEnabled = parameters.get(SIMULATEST_PARALLEL_ENABLED)
				.map(Boolean::parseBoolean)
				.orElseGet(() -> parameters.get(JUPITER_PARALLEL_ENABLED)
						.map(Boolean::parseBoolean)
						.orElse(false));

		boolean allowParallelWhenInsistenceIsActive = parameters.get(SIMULATEST_ALLOW_INSISTENCE_PARALLEL)
				.map(Boolean::parseBoolean)
				.orElse(false);

		Map<String, String> jupiterParameters = new LinkedHashMap<>();
		jupiterParameters.put(JUPITER_PARALLEL_ENABLED, Boolean.toString(parallelEnabled));
		jupiterParameters.put(SIMULATEST_ALLOW_INSISTENCE_PARALLEL,
				Boolean.toString(allowParallelWhenInsistenceIsActive));

		copyIfPresent(parameters, JUPITER_MODE_DEFAULT, jupiterParameters);
		copyIfPresent(parameters, JUPITER_MODE_CLASSES_DEFAULT, jupiterParameters);

		copyIfPresent(parameters, JUPITER_PARALLEL_CONFIG_PREFIX + "strategy", jupiterParameters);
		copyIfPresent(parameters, JUPITER_PARALLEL_CONFIG_PREFIX + "fixed.parallelism", jupiterParameters);
		copyIfPresent(parameters, JUPITER_PARALLEL_CONFIG_PREFIX + "fixed.max-pool-size", jupiterParameters);
		copyIfPresent(parameters, JUPITER_PARALLEL_CONFIG_PREFIX + "dynamic.factor", jupiterParameters);
		copyIfPresent(parameters, JUPITER_PARALLEL_CONFIG_PREFIX + "dynamic.max-pool-size-factor", jupiterParameters);
		copyIfPresent(parameters, JUPITER_PARALLEL_CONFIG_PREFIX + "custom.class", jupiterParameters);

		return new ParallelExecutionSettings(parallelEnabled, allowParallelWhenInsistenceIsActive, jupiterParameters);
	}

	private static void copyIfPresent(ConfigurationParameters parameters, String key, Map<String, String> target) {
		parameters.get(key).ifPresent(value -> target.put(key, value));
	}

	public boolean parallelEnabled() {
		return parallelEnabled;
	}

	public boolean allowParallelWhenInsistenceIsActive() {
		return allowParallelWhenInsistenceIsActive;
	}

	public Map<String, String> jupiterParameters() {
		return jupiterParameters;
	}

}
