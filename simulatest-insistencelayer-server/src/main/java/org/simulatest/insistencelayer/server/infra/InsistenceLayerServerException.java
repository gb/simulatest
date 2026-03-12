package org.simulatest.insistencelayer.server.infra;

import java.io.Serial;

public class InsistenceLayerServerException extends RuntimeException {

	@Serial
	private static final long serialVersionUID = -3838213056713524036L;

	public InsistenceLayerServerException(String message) {
		super(message);
	}
	
	public InsistenceLayerServerException(Throwable cause) {
		super(cause);
	}

    public InsistenceLayerServerException(String message, Throwable cause) {
        super(message, cause);
    }
	
}