package com.polly.utils.command.poll;

public class RegisterPollChangeListenerCommand {
	private final long id;
	private final boolean hasVoted;
	
	public RegisterPollChangeListenerCommand(long id, boolean hasVoted) {
		this.id = id;
		this.hasVoted = hasVoted;
	}

	public long getId() {
		return id;
	}

	public boolean hasVoted() {
		return hasVoted;
	}
}