package com.polly.utils.command.poll;

public class EditPollNameCommand {
	private final long id;
	private final String name;
	
	public EditPollNameCommand(long id, String name) {
		this.id = id;
		this.name = name;
	}

	public long getId() {
		return id;
	}

	public String getName() {
		return name;
	}
}