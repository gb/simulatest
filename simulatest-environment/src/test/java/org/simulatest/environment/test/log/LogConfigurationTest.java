package org.simulatest.environment.test.log;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;

import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.spi.LoggingEvent;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class LogConfigurationTest {

	private final static Logger logger = Logger.getLogger(LogConfigurationTest.class);
	private final static MyAppender appender = new MyAppender();
	private List<LoggingEvent> log = null;

	@BeforeClass
	public static void init() {
		logger.addAppender(appender);
	}
	
	@Before
	public void setup() {
		throwsAllKindsOfLogs();
		log = appender.getLog();
	}
	
	private void throwsAllKindsOfLogs() {
		logger.info("Environment Info Message");
		logger.warn("Environment Warn Message");
		logger.error("Environment Error Message");
		logger.fatal("Environment Fatal Message");
	}
	
	@Test
	public void testNameOfLogger() {
		assertEquals(log.get(0).getLoggerName(), getClass().getCanonicalName());
	}
	
	@Test
	public void testLevelsOfLogs() {
		assertThat(log.get(0).getLevel(), is(Level.INFO));
		assertThat(log.get(1).getLevel(), is(Level.WARN));
		assertThat(log.get(2).getLevel(), is(Level.ERROR));
		assertThat(log.get(3).getLevel(), is(Level.FATAL));
	}
	
	@Test
	public void testMessagesOfLogs() {
		assertEquals(log.get(0).getMessage(), "Environment Info Message");
		assertEquals(log.get(1).getMessage(), "Environment Warn Message");
		assertEquals(log.get(2).getMessage(), "Environment Error Message");
		assertEquals(log.get(3).getMessage(), "Environment Fatal Message");
	}

	@After
	public void tearDown() {
		logger.removeAppender(appender);
	}

}