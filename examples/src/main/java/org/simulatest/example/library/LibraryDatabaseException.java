package org.simulatest.example.library;

public final class LibraryDatabaseException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LibraryDatabaseException(String message) {
		super(message);
	}

	public LibraryDatabaseException(String message, Throwable cause) {
		super(message, cause);
	}

}
