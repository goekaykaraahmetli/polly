package com.polly.utils.wrapper;

public class ErrorWrapper {
	private final String message;
	
	public ErrorWrapper(String message) {
		this.message = message;
	}

	public String getMessage() {
		return message;
	}
}