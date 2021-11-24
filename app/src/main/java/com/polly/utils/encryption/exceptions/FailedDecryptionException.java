package com.polly.utils.encryption.exceptions;

public class FailedDecryptionException extends Exception {

	private static final long serialVersionUID = -109046116820576249L;
	
	public FailedDecryptionException() {
		super();
	}
	
	public FailedDecryptionException(String message) {
		super(message);
	}
	
	public FailedDecryptionException(Throwable cause) {
		super(cause);
	}
	
	public FailedDecryptionException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FailedDecryptionException(String message, Throwable cause, boolean enableSuppression, boolean writeStackTrace) {
		super(message, cause, enableSuppression, writeStackTrace);
	}
}
