package com.polly.utils.command;

public class LoadPollCommand {
    private long pollId;

    public LoadPollCommand(long pollId){
        this.pollId = pollId;
    }

    public long getPollId(){
        return pollId;
    }
}