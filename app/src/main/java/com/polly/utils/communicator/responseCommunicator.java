package com.polly.utils.communicator;

import com.polly.utils.Message;
import com.polly.utils.Organizer;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;



public abstract class responseCommunicator extends Communicator{
    final ArrayBlockingQueue<Message> responseQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);

    public final List<Long> responseIds = new ArrayList<>();

    protected responseCommunicator() {
        super();

        new Thread(() -> {
            while(true) {
                Message input = null;
                try {
                    input = getInput();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(responseIds.contains(input.getResponseId())){
                    while(!responseQueue.offer(input));
                } else {
                    handleInput(input);
                }
            }
        }).start();
    }

    public <T> Message sendWithResponse(long receiver, long responseId, T data) {
        responseIds.add(responseId);
        Organizer.getSocketHandler().send(getCommunicationId(), receiver, responseId, data);

        //TODO set timeout
        while(true) {
            try {
                Message input = responseQueue.take();
                if(input.getResponseId() == responseId){
                    return input;
                }
                while(!responseQueue.offer(input));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}