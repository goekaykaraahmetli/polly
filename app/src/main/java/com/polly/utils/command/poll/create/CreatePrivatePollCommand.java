package com.polly.utils.command.poll.create;

import java.time.LocalDateTime;
import java.util.List;

import com.polly.utils.poll.PollDescription;

public class CreatePrivatePollCommand extends CreatePollCommand {
	private final long usergroup;
	
	public CreatePrivatePollCommand(String name, PollDescription description, LocalDateTime expirationTime,
									List<String> options, long usergroup) {
		super(name, description, expirationTime, options);
		this.usergroup = usergroup;
	}

	public long getUsergroup() {
		return usergroup;
	}
}