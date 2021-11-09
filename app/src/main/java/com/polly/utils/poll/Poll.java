package com.polly.utils.poll;

import java.util.Map;
import java.util.NoSuchElementException;

public class Poll {
	private final long id;
	private String name;
	private Map<String, Integer> data;
	
	public Poll(String name, Map<String, Integer> poll) {
		this.id = PollManager.getNextId();
		this.name = name;
		this.data = poll;
		PollManager.registerPoll(this);
	}
	
	public long getId() {
		return id;
	}
	
	public String getName() {
		return name;
	}
	
	public Map<String, Integer> getData(){
		return data;
	}
	
	void vote(String option) throws NoSuchElementException{
		if(!data.containsKey(option))
			throw new NoSuchElementException();
		data.put(option, data.get(option)+1);
	}
}