package com.polly.utils.commandold;

import java.util.List;

public class CreatePollCommand {
    private final String pollName;
    private final List<String> pollOptions;

    public CreatePollCommand(String pollName, List<String> pollOptions){
        this.pollName = pollName;
        this.pollOptions = pollOptions;
    }

    public String getPollName(){
        return pollName;
    }

    public List<String> getPollOptions(){
        return pollOptions;
    }
}
