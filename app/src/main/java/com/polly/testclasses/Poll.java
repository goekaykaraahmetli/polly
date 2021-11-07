package com.polly.testclasses;

import java.util.Map;

public class Poll {
    private final Map<String, Integer> poll;
    private String name;


    public Poll(String name, Map<String, Integer> poll) {
        this.name = name;
        this.poll = poll;
    }

    public void printPoll() {
        for(String option : poll.keySet()) {
            System.out.println(option + " got " + poll.get(option) +" votes!");
        }
    }

    public Map<String, Integer> getPoll() {
        return poll;
    }

    public String getName() {
        return name;
    }
}
