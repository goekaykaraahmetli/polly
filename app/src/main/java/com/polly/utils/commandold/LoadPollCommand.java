package com.polly.utils.commandold;

public class LoadPollCommand {
    private long pollId;

    public LoadPollCommand(long pollId){
        this.pollId = pollId;
    }

    public long getPollId(){
        return pollId;
    }
}