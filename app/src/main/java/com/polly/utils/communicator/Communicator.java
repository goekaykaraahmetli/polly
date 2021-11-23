package com.polly.utils.communicator;


import android.content.Context;

import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;

import com.polly.utils.Message;
import com.polly.utils.Organizer;

public abstract class Communicator{
    static final int MAX_QUEUE_LENGTH = 20;
    final long id;
    final ArrayBlockingQueue<Message> inputQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);

    protected Communicator() {
        id = CommunicatorManager.getNextId();
        registerCommunicator();
    }

    private void registerCommunicator() {
        CommunicatorManager.registerCommunicator(this);
    }

    public boolean addInput(Message message){
        return inputQueue.offer(message);
    }

    public Message getInput() throws InterruptedException {
        return inputQueue.take();
    }

    public long getCommunicationId(){
        return id;
    }

    public abstract void handleInput(Message message);

    public <T> void send(long receiver, T data) throws IOException {
        Organizer.send(getCommunicationId(), receiver, data);
    }
}