package org.simulatest.environment.test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simulatest.environment.EnvironmentDefinition;
import org.simulatest.environment.listener.EnvironmentRunnerListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentRunnerListenerLog implements EnvironmentRunnerListener {

	private static final Logger logger = LoggerFactory.getLogger(EnvironmentRunnerListenerLog.class);

	private final List<String> logs = new ArrayList<>();

	@Override
	public void beforeRun(EnvironmentDefinition definition) {
		log("beforeRun", definition);
	}

	@Override
	public void afterRun(EnvironmentDefinition definition) {
		log("afterRun", definition);
	}

	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		log("afterChildrenRun", definition);
	}

	@Override
	public void afterSiblingCleanup(EnvironmentDefinition definition) {
		log("afterSiblingCleanup", definition);
	}

	private void log(String phase, EnvironmentDefinition definition) {
		logger.debug("{} >> {}", phase, definition);
		logs.add(String.format("[%s] %s", definition, phase));
	}

	public List<String> getLogs() {
		return Collections.unmodifiableList(logs);
	}

}
