package com.polly.utils.encryption.exceptions;

public class FailedKeyGenerationException extends Exception {

	private static final long serialVersionUID = -4216826260830849628L;
	
	
	public FailedKeyGenerationException() {
		super();
	}
	
	public FailedKeyGenerationException(String message) {
		super(message);
	}
	
	public FailedKeyGenerationException(Throwable cause) {
		super(cause);
	}
	
	public FailedKeyGenerationException(String message, Throwable cause) {
		super(message, cause);
	}
	
	public FailedKeyGenerationException(String message, Throwable cause, boolean enableSuppression, boolean writeStackTrace) {
		super(message, cause, enableSuppression, writeStackTrace);
	}
}
