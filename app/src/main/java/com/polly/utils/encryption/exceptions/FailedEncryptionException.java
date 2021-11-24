package com.polly.utils.encryption.exceptions;

public class FailedEncryptionException extends Exception {

	private static final long serialVersionUID = -8933197356486474267L;
	
	public FailedEncryptionException() {
		super();
	}
	
	public FailedEncryptionException(String message) {
		super(message);
	}
	
	public FailedEncryptionException(Throwable cause) {
		super(cause);
	}
	
	public FailedEncryptionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FailedEncryptionException(String message, Throwable cause, boolean enableSuppression, boolean writeStackTrace) {
		super(message, cause, enableSuppression, writeStackTrace);
	}
}
