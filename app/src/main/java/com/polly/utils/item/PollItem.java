package com.polly.utils.item;

public class PollItem {
    private long id;
    private String pollname;
    private String creator;

    public PollItem(long id, String pollname, String creator){
        this.id = id;
        this.pollname = pollname;
        this.creator = creator;
    }

    public long getId() {
        return id;

    }

    public String getCreator() {
        return creator;
    }

    public String getPollname() {
        return pollname;
    }
}
