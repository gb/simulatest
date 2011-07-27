package objectiveLabs.insistenceLayer.infra;

public class InsistenceLayerException extends RuntimeException {

	private static final long serialVersionUID = 86473555661526330L;
		
	public InsistenceLayerException(String message) {
		super(message);
	}
	
	public InsistenceLayerException(Throwable cause) {
		super(cause);
	}

    public InsistenceLayerException(String message, Throwable cause) {
        super(message, cause);
    }
	
}