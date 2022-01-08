package com.polly.utils.commandold;

public class VotePollCommand {
    private long pollId;
    private String pollOption;

    public VotePollCommand(long pollId, String pollOption){
        this.pollId = pollId;
        this.pollOption = pollOption;
    }

    public long getPollId(){
        return pollId;
    }

    public String getPollOption(){
        return pollOption;
    }
}
