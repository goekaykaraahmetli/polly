package com.polly.utils;

import java.util.Map;

public class Poll {
    private static Poll currentPoll;
    static{
        currentPoll = null;
    }

    private String title;
    private Map<String, Integer> poll;
    private String description;

    public Poll(String title, Map<String, Integer> poll, String description){
        this.title = title;
        this.poll = poll;
        this.description = description;
    }

    public Poll(String title, Map<String, Integer> poll){
        this(title, poll, "");
    }

    public Map<String, Integer> getPoll(){
        return poll;
    }

    public String getTitle(){
        return title;
    }

    public void rename(String title){
        this.title = title;
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

    public static Poll getCurrentPoll(){
        return currentPoll;
    }

    public static void setCurrentPoll(Poll poll){
        currentPoll = poll;
    }
}
