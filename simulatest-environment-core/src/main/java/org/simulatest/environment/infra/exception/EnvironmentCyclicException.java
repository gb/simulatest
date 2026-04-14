package org.simulatest.environment.infra.exception;

public final class EnvironmentCyclicException extends EnvironmentGeneralException {

	private static final long serialVersionUID = 3325750366351713282L;

	public EnvironmentCyclicException(String message) {
		super(message);
	}

}
