package com.polly.utils.item;

public class PollItem {
    private int id;
    private String pollname;
    private String creator;

    public PollItem(int id, String pollname, String creator){
        this.id = id;
        this.pollname = pollname;
        this.creator = creator;
    }

    public int getId() {
        return id;

    }

    public String getCreator() {
        return creator;
    }

    public String getPollname() {
        return pollname;
    }
}
