package com.polly.utils.wrapper;

import java.util.List;

import com.polly.utils.poll.Poll;

public class PollListWrapper {
	private List<Poll> list;
	
	public PollListWrapper(List<Poll> list) {
		this.setList(list);
	}

	public List<Poll> getList() {
		return list;
	}

	public void setList(List<Poll> list) {
		this.list = list;
	}
}
