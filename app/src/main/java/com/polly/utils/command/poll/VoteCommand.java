package com.polly.utils.command.poll;

public class VoteCommand {
	private final long id;
	private final String option;
	
	public VoteCommand(long id, String option) {
		this.id = id;
		this.option = option;
	}

	public long getId() {
		return id;
	}

	public String getOption() {
		return option;
	}
}