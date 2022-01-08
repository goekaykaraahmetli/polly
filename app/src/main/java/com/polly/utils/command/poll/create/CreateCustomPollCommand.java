package com.polly.utils.command.poll.create;

import java.time.LocalDateTime;
import java.util.List;

import com.polly.utils.poll.PollDescription;

public class CreateCustomPollCommand extends CreatePollCommand {
	private final List<Long> canSee;
	private final List<Long> canSeeResults;
	
	
	public CreateCustomPollCommand(String name, PollDescription description, LocalDateTime expirationTime,
								   List<String> options, List<Long> canSee, List<Long> canSeeResults) {
		super(name, description, expirationTime, options);
		this.canSee = canSee;
		this.canSeeResults = canSeeResults;
	}
	
	public List<Long> getCanSee() {
		return canSee;
	}
	
	public List<Long> getCanSeeResults() {
		return canSeeResults;
	}
}