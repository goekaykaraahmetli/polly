package com.polly.utils.wrapper;

public class LogoutAnswerWrapper {
	private final boolean successful;
	private final String message;
	
	public LogoutAnswerWrapper(boolean successful, String message) {
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