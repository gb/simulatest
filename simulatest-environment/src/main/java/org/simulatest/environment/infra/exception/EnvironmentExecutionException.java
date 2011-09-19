package org.simulatest.environment.infra.exception;

public class EnvironmentExecutionException extends EnvironmentGeneralException {

	private static final long serialVersionUID = -3846577248077471189L;

	public EnvironmentExecutionException(String message) {
		super(message);
	}
	
    public EnvironmentExecutionException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EnvironmentExecutionException(Throwable cause) {
        super(cause);
    }
	
}