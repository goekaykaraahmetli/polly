package com.polly.utils.command.poll;

public class RemovePollChangeListenerCommand {
	private final long id;
	
	public RemovePollChangeListenerCommand(long id) {
		this.id = id;
	}

	public long getId() {
		return id;
	}
}