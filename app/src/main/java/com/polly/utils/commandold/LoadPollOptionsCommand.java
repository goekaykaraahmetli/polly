package com.polly.utils.commandold;

public class LoadPollOptionsCommand {
    private long pollId;

    public LoadPollOptionsCommand(long pollId){
        this.pollId = pollId;
    }

    public long getPollId(){
        return pollId;
    }
}