package com.polly.utils.command.poll;

public class IsMyPollCommand {
	private final long id;
	
	public IsMyPollCommand(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}