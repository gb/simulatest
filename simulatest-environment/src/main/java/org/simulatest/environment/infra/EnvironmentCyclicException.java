package org.simulatest.environment.infra;

public class EnvironmentCyclicException extends RuntimeException {

	private static final long serialVersionUID = 3325750366351713282L;

	public EnvironmentCyclicException(String message) {
		super(message);
	}

	public EnvironmentCyclicException(String message, Throwable cause) {
		super(message, cause);
	}

	public EnvironmentCyclicException(Throwable cause) {
		super(cause);
	}

}