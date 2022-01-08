package com.polly.utils.command.poll;

public class GetPollResultsCommand {
	private final long id;
	
	public GetPollResultsCommand(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
}