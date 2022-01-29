package com.polly.utils.command.poll.create;

import java.time.LocalDateTime;
import java.util.List;

import com.polly.utils.poll.PollDescription;

public class CreateGeofencePollCommand extends CreatePollCommand {
	private final Area area;
	
	public CreateGeofencePollCommand(String name, PollDescription description, LocalDateTime expirationTime,
			List<String> options, Area area) {
		super(name, description, expirationTime, options);
		this.area = area;
	}
	
	public Area getArea() {
		return area;
	}
}