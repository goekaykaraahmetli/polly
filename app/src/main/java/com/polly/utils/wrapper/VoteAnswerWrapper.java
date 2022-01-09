package com.polly.utils.wrapper;

public class VoteAnswerWrapper {
	private final boolean successful;
	private final String message;
	
	public VoteAnswerWrapper(boolean successful, String message) {
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
