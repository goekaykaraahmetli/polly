package com.polly.utils;

import java.util.concurrent.PriorityBlockingQueue;

public abstract class InputReceiver<T> {
    private static final int MAX_QUEUE_LENGTH = 20;
    private final long id;
    private final PriorityBlockingQueue<T> inputQueue = new PriorityBlockingQueue<>(MAX_QUEUE_LENGTH);

    public InputReceiver(){
        this.id = InputReceiverManager.getNextId();
        InputReceiverManager.registerInputReceiver(this);
    }

    public boolean addInput(T input){
        return inputQueue.offer(input);
    }

    protected T getInput() throws InterruptedException {
        return inputQueue.take();
    }

    public long getId(){
        return id;
    }
}