package com.polly.utils.wrapper;

import java.util.Map;

import com.polly.utils.poll.BasisPollInformation;

public class PollResultsWrapper {
	private final Map<String, Integer> pollResults;
	private final BasisPollInformation basicPollInformation;
	
	public PollResultsWrapper(Map<String, Integer> pollResults, BasisPollInformation basicPollInformation) {
		this.pollResults = Map.copyOf(pollResults);
		this.basicPollInformation = basicPollInformation;
	}

	public Map<String, Integer> getPollResults() {
		return pollResults;
	}

	public BasisPollInformation getBasicPollInformation() {
		return basicPollInformation;
	}
}
