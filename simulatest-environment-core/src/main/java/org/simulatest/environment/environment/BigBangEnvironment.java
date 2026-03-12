package org.simulatest.environment.environment;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BigBangEnvironment implements Environment {

	private static final Logger logger = LoggerFactory.getLogger(BigBangEnvironment.class);
	
	@Override public void run() {
		logger.info("Let there be light");
	}

}
