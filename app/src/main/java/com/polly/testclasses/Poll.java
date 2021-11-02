package com.polly.testclasses;

import java.util.Map;

public class Poll {
    private Map<String, Integer> poll;

    public Poll(Map<String, Integer> poll){
        this.poll = poll;
    }

    public Map<String, Integer> getPoll(){
        return poll;
    }

    public void updatePoll(Poll poll){
        this.poll = poll.getPoll();
    }
}
