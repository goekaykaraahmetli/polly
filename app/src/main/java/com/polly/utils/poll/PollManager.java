package com.polly.utils.poll;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class PollManager {
	private static long nextId = 0L;
	private static Map<Long, Poll> polls = new HashMap<>();
	
	private PollManager() {
		super();
	}
	
	public static long getNextId() {
		return nextId++;
	}
	
	public static void registerPoll(Poll poll) {
		polls.put(poll.getId(), poll);
	}
	
	public static Poll getPollById(long id) throws NoSuchElementException{
		if(!polls.containsKey(id))
			throw new NoSuchElementException();
		return polls.get(id);
	}
	
	public void createPoll(String name, Map<String, Integer> poll) {
		new Poll(name, poll);
	}
	
	public Poll loadPoll(long id) {
		return getPollById(id);
	}
	
	public void vote(long id, String option) {
		getPollById(id).vote(option); 
	}
	
	public void editPoll(long id) {
		throw new UnsupportedOperationException();
	}
}