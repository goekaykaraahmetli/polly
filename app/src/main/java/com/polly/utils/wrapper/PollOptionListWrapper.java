package com.polly.utils.wrapper;

import java.util.List;

public class PollOptionListWrapper {
	private final List<PollOptionsWrapper> list;
	
	public PollOptionListWrapper(List<PollOptionsWrapper> list) {
		this.list = list;
	}

	public List<PollOptionsWrapper> getList() {
		return list;
	}
}