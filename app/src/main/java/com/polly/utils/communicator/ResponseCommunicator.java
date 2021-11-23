package com.polly.utils.communicator;

import android.content.Context;

import com.polly.utils.Message;
import com.polly.utils.Organizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;



public abstract class ResponseCommunicator extends Communicator{
    final ArrayBlockingQueue<Message> responseQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);

    public final List<Long> responseIds = new ArrayList<>();

    protected ResponseCommunicator() {
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

    public <T> Message sendWithResponse(long receiver, long responseId, T data) throws IOException {
        responseIds.add(responseId);
        Organizer.send(getCommunicationId(), receiver, responseId, data);

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