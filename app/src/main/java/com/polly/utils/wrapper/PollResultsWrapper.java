package com.polly.utils.wrapper;

import java.util.Map;

public class PollResultsWrapper {
	private final Map<String, Integer> pollResults;
	
	public PollResultsWrapper(Map<String, Integer> pollResults) {
		this.pollResults = Map.copyOf(pollResults);
	}

	public Map<String, Integer> getPollResults() {
		return pollResults;
	}
}