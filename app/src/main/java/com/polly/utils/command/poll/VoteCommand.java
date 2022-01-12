package com.polly.utils.command.poll;

import com.polly.utils.Location;

public class VoteCommand {
	private final long id;
	private final String option;
	private final Location location;
	
	public VoteCommand(long id, String option, Location location) {
		this.id = id;
		this.option = option;
		this.location = location;
	}
	
	public VoteCommand(long id, String option) {
		this(id, option, null);
	}

	public long getId() {
		return id;
	}

	public String getOption() {
		return option;
	}

	public Location getLocation() {
		return location;
	}
}