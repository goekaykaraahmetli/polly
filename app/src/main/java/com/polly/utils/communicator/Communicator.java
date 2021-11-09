package com.polly.utils.communicator;


import java.util.concurrent.ArrayBlockingQueue;

import com.polly.utils.Message;

public interface Communicator{
    static final int MAX_QUEUE_LENGTH = 20;
    final long id = CommunicatorManager.getNextId();
    final ArrayBlockingQueue<Message> inputQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);
    
    
    public default void registerCommunicator() {
    	CommunicatorManager.registerCommunicator(this);
    }
    
    public default boolean addInput(Message message){
        return inputQueue.offer(message);
    }

    public default Message getInput() throws InterruptedException {
        return inputQueue.take();
    }

    public default long getId(){
        return id;
    }
}