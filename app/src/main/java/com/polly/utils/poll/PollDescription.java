package com.polly.utils.poll;

public class PollDescription {
	private final String pollDescriptionText;

	public PollDescription(String description) {
		pollDescriptionText = description;
	}

	public String getDescription() {
		return pollDescriptionText;
	}
}