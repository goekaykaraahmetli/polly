package com.polly.utils.command.poll.create;

import java.time.LocalDateTime;
import java.util.List;

import com.polly.utils.poll.PollDescription;

public class CreatePublicPollCommand extends CreatePollCommand {
	public CreatePublicPollCommand(String name, PollDescription description, LocalDateTime expirationTime, List<String> options) {
		super(name, description, expirationTime, options);
	}
}