package com.polly.utils.command.poll;

public class FindPollCommand {
	private final String name;
	private final String creator;
	private final boolean active;
	private final boolean expired;

	public FindPollCommand(String name, String creator, boolean active, boolean expired) {
		this.name = name;
		this.creator = creator;
		this.active = active;
		this.expired = expired;
	}

	public String getName() {
		return name;
	}

	public String getCreator() {
		return creator;
	}

	public boolean getActive() {
		return active;
	}

	public boolean getExpired() {
		return expired;
	}
}