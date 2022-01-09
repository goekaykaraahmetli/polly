package com.polly.utils.command.poll.create;

import java.time.LocalDateTime;
import java.util.List;

import com.polly.utils.poll.PollDescription;

public class CreateCustomPollCommand extends CreatePollCommand {
	private final List<String> canSee;
	private final List<String> canSeeResults;


	public CreateCustomPollCommand(String name, PollDescription description, LocalDateTime expirationTime,
								   List<String> options, List<String> canSee, List<String> canSeeResults) {
		super(name, description, expirationTime, options);
		this.canSee = canSee;
		this.canSeeResults = canSeeResults;
	}

	public List<String> getCanSee() {
		return canSee;
	}

	public List<String> getCanSeeResults() {
		return canSeeResults;
	}
}