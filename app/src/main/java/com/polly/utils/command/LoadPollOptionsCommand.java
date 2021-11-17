package com.polly.utils.command;

public class LoadPollOptionsCommand {
    private long pollId;

    public LoadPollOptionsCommand(long pollId){
        this.pollId = pollId;
    }

    public long getPollId(){
        return pollId;
    }
}