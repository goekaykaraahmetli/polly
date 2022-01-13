package com.polly.utils.command.poll;

import com.polly.utils.poll.PollDescription;

public class EditPollDescriptionCommand {
	private final long id;
	private final PollDescription description;
	
	public EditPollDescriptionCommand(long id, PollDescription description) {
		this.id= id;
		this.description = description;
	}

	public long getId() {
		return id;
	}

	public PollDescription getDescription() {
		return description;
	}
}