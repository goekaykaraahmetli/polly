package com.polly.utils.command.poll;

public class VoteAnswerCommand {
	private final boolean successful;
	private final String message;
	
	public VoteAnswerCommand(boolean successful, String message) {
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
