package com.polly.utils.wrapper;

import java.util.List;

import com.polly.utils.poll.BasisPollInformation;

public class PollOptionsWrapper {
	private final List<String> pollOptions;
	private final BasisPollInformation basicPollInformation;
	
	public PollOptionsWrapper(List<String> pollOptions, BasisPollInformation basicPollInformation) {
		this.pollOptions = List.copyOf(pollOptions);
		this.basicPollInformation = basicPollInformation;
	}

	public List<String> getPollOptions() {
		return pollOptions;
	}

	public BasisPollInformation getBasicPollInformation() {
		return basicPollInformation;
	}
}