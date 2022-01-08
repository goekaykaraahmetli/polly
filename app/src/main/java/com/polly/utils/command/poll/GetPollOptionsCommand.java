package com.polly.utils.command.poll;

public class GetPollOptionsCommand {
	private final long id;

	public GetPollOptionsCommand(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}