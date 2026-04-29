package org.simulatest.environment.infra.exception;

public abstract class EnvironmentGeneralException extends RuntimeException {

	private static final long serialVersionUID = 4812070394349998718L;

	protected EnvironmentGeneralException(String message) {
		super(message);
	}

	protected EnvironmentGeneralException(String message, Throwable cause) {
		super(message, cause);
	}

}
