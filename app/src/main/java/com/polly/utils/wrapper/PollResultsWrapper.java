package com.polly.utils.wrapper;

import java.util.Collections;
import java.util.Map;

import com.polly.utils.poll.BasicPollInformation;

public class PollResultsWrapper {
	private final Map<String, Integer> pollResults;
	private final BasicPollInformation basicPollInformation;
	
	public PollResultsWrapper(Map<String, Integer> pollResults, BasicPollInformation basicPollInformation) {
		this.pollResults = Collections.unmodifiableMap(pollResults);
		this.basicPollInformation = basicPollInformation;
	}

	public Map<String, Integer> getPollResults() {
		return pollResults;
	}

	public BasicPollInformation getBasicPollInformation() {
		return basicPollInformation;
	}
}