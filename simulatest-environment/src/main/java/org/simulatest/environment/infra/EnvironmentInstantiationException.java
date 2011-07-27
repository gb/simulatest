package org.simulatest.environment.infra;

public class EnvironmentInstantiationException extends RuntimeException {

	private static final long serialVersionUID = 6171958131446654586L;

	public EnvironmentInstantiationException(String message) {
		super(message);
	}
	
    public EnvironmentInstantiationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public EnvironmentInstantiationException(Throwable cause) {
        super(cause);
    }

}