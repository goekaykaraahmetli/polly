package com.polly.utils.poll;

import java.io.Serializable;
import java.util.Map;
import java.util.NoSuchElementException;

public class Poll implements Serializable {
	private final long id;
	private String name;
	private Map<String, Integer> data;
	private String description;


	public Poll(long id, String name, Map<String, Integer> poll) {
		this(id, name, poll, "");
	}

	public Poll(long id, String name, Map<String, Integer> poll, String description) {
		this.id = id;
		this.name = name;
		this.data = poll;
		this.description = description;
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

	public String getDescription(){
		return description;
	}





	void vote(String option) throws NoSuchElementException{
		if(!data.containsKey(option))
			throw new NoSuchElementException();
		data.put(option, data.get(option)+1);
	}


	//TODO only test-purpose
	public void printPoll(){
		System.out.println("Poll: " + name);
		for(String s : data.keySet()){
			System.out.println("Option " + s + " got " + data.get(s) + " votes!");
		}
	}
}