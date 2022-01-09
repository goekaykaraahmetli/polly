package com.polly.utils.wrapper;

public class LoginAnswerWrapper {
	private final boolean successful;
	private final String message;
	
	public LoginAnswerWrapper(boolean successful, String message) {
		this.successful = successful;
		this.message = message;
	}

	public boolean isSuccessful() {
		return successful;
	}

	public String getMessage() {
		return message;
	}
}