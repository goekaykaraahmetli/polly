package com.polly.testclasses;

import java.io.Serializable;
import java.util.Map;

public class Poll implements Serializable {

    private String name;
    private Map<String, Integer> poll;
    private String description;

    public Poll(String name, Map<String, Integer> poll, String description){
        this.name = name;
        this.poll = poll;
        this.description = description;
    }

    public Poll(String name, Map<String, Integer> poll){
        this(name, poll, "");
    }

    public Map<String, Integer> getPoll(){
        return poll;
    }

    public String getName(){
        return name;
    }

    public void rename(String title){
        this.name = name;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public void updatePoll(Poll poll){
        this.poll = poll.getPoll();
    }

    public void printPoll(){
        System.out.println("Poll: " + name);
        for(String s : poll.keySet()){
            System.out.println("Option " + s + " got " + poll.get(s) + " votes!");
        }
    }
}
