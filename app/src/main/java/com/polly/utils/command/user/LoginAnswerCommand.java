package com.polly.utils.command.user;

public class LoginAnswerCommand {
	private final boolean successful;
	private final String message;
	
	public LoginAnswerCommand(boolean successful, String message) {
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