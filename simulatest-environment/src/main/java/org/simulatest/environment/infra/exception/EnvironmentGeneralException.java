package org.simulatest.environment.infra.exception;

public class EnvironmentGeneralException extends RuntimeException {

	private static final long serialVersionUID = 4812070394349998718L;
	
	public EnvironmentGeneralException(String message) {
		super(message);
	}

	public EnvironmentGeneralException(String message, Throwable cause) {
		super(message, cause);
	}

	public EnvironmentGeneralException(Throwable cause) {
		super(cause);
	}

}
