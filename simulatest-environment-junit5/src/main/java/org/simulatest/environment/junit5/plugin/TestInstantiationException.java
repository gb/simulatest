package org.simulatest.environment.junit5.plugin;

public class TestInstantiationException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TestInstantiationException(String message, Throwable cause) {
		super(message, cause);
	}

}
