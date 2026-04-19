package org.simulatest.insistencelayer.infra.exception;

/**
 * Unchecked exception raised by the Insistence Layer when a savepoint or
 * remote-protocol operation fails. Wraps the underlying checked cause
 * (typically {@link java.sql.SQLException} or {@link java.io.IOException}) so
 * callers do not have to declare or catch checked exceptions just to use the
 * Insistence Layer.
 */
public final class InsistenceLayerException extends RuntimeException {

	private static final long serialVersionUID = 86473555661526330L;
		
	public InsistenceLayerException(String message) {
		super(message);
	}
	
	public InsistenceLayerException(String message, Throwable cause) {
		super(message, cause);
	}
	
}