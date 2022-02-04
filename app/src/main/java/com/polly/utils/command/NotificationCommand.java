package com.polly.utils.command;

public class NotificationCommand {
	private final long id;
	
	public NotificationCommand(long id) {
		this.id = id;
	}
	
	public long getId() {
		return id;
	}
}