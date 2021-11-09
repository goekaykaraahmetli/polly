package com.polly.interfaces.communication;

import com.polly.testclasses.ActivityHandler;
import com.polly.testclasses.Poll;

import java.io.IOException;
import java.util.PriorityQueue;
import java.util.concurrent.PriorityBlockingQueue;

class OutputHandler extends Thread{
    private static final int MAX_QUEUE_LENGTH = 20;
    private boolean running = false;
    private final DataStreamManager dataStreamManager;
    private final PriorityBlockingQueue<WrappedData> outputQueue = new PriorityBlockingQueue<>(MAX_QUEUE_LENGTH);

    public OutputHandler(DataStreamManager dataStreamManager){
        this.dataStreamManager = dataStreamManager;
    }

    public void writeOutput(WrappedData wrappedData){
        if(!outputQueue.offer(wrappedData)){
            System.err.println("Could not insert input into outputQueue!");
            try{
                throw new IllegalStateException();
            } catch(IllegalStateException e){
                e.printStackTrace();
            }
        }
    }

    @Override
    public void run() {
        while(running) {
            try {
                WrappedData output = outputQueue.take();
                dataStreamManager.writeOutput(output);
            } catch (InterruptedException | IOException e) {
                System.err.println("Could not handle output!");
                e.printStackTrace();
            }
        }
    }

    @Override
    public void start(){
        super.start();
        running = true;
    }

    public void stopHandler(){
        running = false;
    }
}
