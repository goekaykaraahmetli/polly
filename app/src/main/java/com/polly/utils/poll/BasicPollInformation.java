package com.polly.utils.poll;

import java.time.LocalDateTime;

public class BasicPollInformation {
	private final long id;
	private final String name;
	private final PollDescription description;
	private final LocalDateTime expirationTime;
	
	public BasicPollInformation(long id, String name, PollDescription description, LocalDateTime expirationTime) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.expirationTime = expirationTime;
	}

	public LocalDateTime getExpirationTime() {
		return expirationTime;
	}

	public PollDescription getDescription() {
		return description;
	}

	public String getName() {
		return name;
	}

	public long getId() {
		return id;
	}
}
