package objectiveLabs.environmentTestHarness.environment.listener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.log4j.Logger;

import objectiveLabs.environmentTestHarness.environment.EnvironmentDefinition;
import objectiveLabs.environmentTestHarness.environment.EnvironmentRunnerListener;

public class EnvironmentRunnerListenerLog implements EnvironmentRunnerListener {
	
	private Logger logger = Logger.getLogger(EnvironmentRunnerListenerLog.class);
	
	private final List<String> logs;
	
	public EnvironmentRunnerListenerLog() {
		logs = new ArrayList<String>();
	}
	
	@Override
	public void beforeRun(EnvironmentDefinition definition) {
		logger.debug("[LogListener] beforeRun >> " + definition);
		logs.add(String.format("[%s] beforeRun", definition));
	}
	
	@Override
	public void afterRun(EnvironmentDefinition definition) {
		logger.debug("[LogListener] afterRun >> " + definition);
		logs.add(String.format("[%s] afterRun", definition));
	}
	
	@Override
	public void afterChildrenRun(EnvironmentDefinition definition) {
		logger.debug("[LogListener] afterChildrenRun >> " + definition);
		logs.add(String.format("[%s] afterChildrenRun", definition));
	}
	
	public List<String> getLogs() {
		return Collections.unmodifiableList(logs);
	}
	
}