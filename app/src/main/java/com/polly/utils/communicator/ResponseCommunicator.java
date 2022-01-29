package com.polly.utils.communicator;

import com.polly.utils.Organizer;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;


public abstract class ResponseCommunicator extends Communicator{
    final ArrayBlockingQueue<Message> responseQueue = new ArrayBlockingQueue<>(MAX_QUEUE_LENGTH);
    private long nextResponseId = 0L;
    public final List<Long> responseIds = new LinkedList<>();
    private final List<Long> gotResponseIds = new LinkedList<>();

    private static Timer timer = new Timer();

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

    public <T> Message sendWithResponse(long receiver, T data) throws IOException {
        long responseId = getNextResponseId();

        TimerTask checkIfGotResponse = new TimerTask() {
            @Override
            public void run() {
                if(!gotResponseWithId(responseId)) {
                    CommunicatorManager.timedOut();
                }
            }
        };

        timer.schedule(checkIfGotResponse, 2000);

        responseIds.add(responseId);
        while(!Organizer.send(getCommunicationId(), receiver, responseId, data)){
            // waiting till Organizer has successfully send
            System.out.println("Organizer did not send successfully");
        }

        while(true) {
            try {
                Message input = responseQueue.take();
                if(input.getResponseId() == responseId){
                    responseIds.remove(responseId);
                    gotResponseIds.add(responseId);
                    return input;
                }
                while(!responseQueue.offer(input));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public long getNextResponseId() {
        return nextResponseId++;
    }

    private boolean gotResponseWithId(long id) {
        return gotResponseIds.contains(id);
    }

    void timedOut() {
        for(long id : responseIds) {
            Message errorMessage = new Message(0L, 0L, id, ErrorWrapper.class, new ErrorWrapper("lost connection to server!"));

            while(!responseQueue.offer(errorMessage));
        }
    }
}