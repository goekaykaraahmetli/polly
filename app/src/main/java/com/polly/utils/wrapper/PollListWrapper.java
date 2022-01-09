package com.polly.utils.wrapper;

import java.util.List;

public class PollListWrapper {
	private final List<PollResultsWrapper> list;

	public PollListWrapper(List<PollResultsWrapper> list) {
		this.list = list;
	}

	public List<PollResultsWrapper> getList() {
		return list;
	}
}