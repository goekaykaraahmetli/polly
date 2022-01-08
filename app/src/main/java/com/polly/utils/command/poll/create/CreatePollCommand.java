package com.polly.utils.command.poll.create;

import java.time.LocalDateTime;
import java.util.List;

import com.polly.utils.poll.PollDescription;

public abstract class CreatePollCommand {
	private final String name;
	private final PollDescription description;
	private final LocalDateTime expirationTime;
	private final List<String> options;
	
	protected CreatePollCommand(String name, PollDescription description, LocalDateTime expirationTime, List<String> options) {
		this.name = name;
		this.description = description;
		this.expirationTime = expirationTime;
		this.options = options;
	}

	public String getName() {
		return name;
	}

	public PollDescription getDescription() {
		return description;
	}

	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}

	public List<String> getOptions() {
		return options;
	}
}
