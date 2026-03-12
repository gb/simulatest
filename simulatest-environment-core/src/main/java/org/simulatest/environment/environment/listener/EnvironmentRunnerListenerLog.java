package org.simulatest.environment.environment.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.simulatest.environment.environment.EnvironmentDefinition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EnvironmentRunnerListenerLog implements EnvironmentRunnerListener {
	
	private static final Logger logger = LoggerFactory.getLogger(EnvironmentRunnerListenerLog.class);
	
	private final List<String> logs;
	
	public EnvironmentRunnerListenerLog() {
		logs = new ArrayList<>();
	}
	
	@Override
	public void beforeRun(EnvironmentDefinition definition) {
		logger.debug("[LogListener] beforeRun >> {}", definition);
		logs.add(String.format("[%s] beforeRun", definition));
	}
	
	@Override
	public void afterRun(EnvironmentDefinition definition) {
		logger.debug("[LogListener] afterRun >> {}", definition);
		logs.add(String.format("[%s] afterRun", definition));
	}
	
	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		logger.debug("[LogListener] afterChildrenRun >> {}", definition);
		logs.add(String.format("[%s] afterChildrenRun", definition));
	}

	@Override
	public void afterSiblingCleanup(EnvironmentDefinition definition) {
		logger.debug("[LogListener] afterSiblingCleanup >> {}", definition);
		logs.add(String.format("[%s] afterSiblingCleanup", definition));
	}
	
	public List<String> getLogs() {
		return Collections.unmodifiableList(logs);
	}
	
}