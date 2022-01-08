package com.polly.utils.wrapper;

import java.util.List;

public class PollOptionsWrapper {
	private final List<String> pollOptions;
	
	public PollOptionsWrapper(List<String> pollOptions) {
		this.pollOptions = List.copyOf(pollOptions);
	}

	public List<String> getPollOptions() {
		return pollOptions;
	}
}