package com.polly.utils.wrapper;

import java.util.List;
import com.polly.utils.poll.BasicPollInformation;

public class PollOptionsWrapper {
	private final List<String> pollOptions;
	private final BasicPollInformation basicPollInformation;
	
	public PollOptionsWrapper(List<String> pollOptions, BasicPollInformation basicPollInformation) {
		this.pollOptions = List.copyOf(pollOptions);
		this.basicPollInformation = basicPollInformation;
	}

	public List<String> getPollOptions() {
		return pollOptions;
	}

	public BasicPollInformation getBasicPollInformation() {
		return basicPollInformation;
	}
}