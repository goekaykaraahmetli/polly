package com.polly.interfaces.communication;

import com.polly.testclasses.Poll;

import java.io.IOException;

class InputHandler extends Thread{
    private boolean running = false;
    private DataStreamManager dataStreamManager;

    public InputHandler(DataStreamManager dataStreamManager){
        this.dataStreamManager = dataStreamManager;
    }

    private void handleInput(WrappedData wrappedData){
        //TODO handle input
        if(wrappedData.getDataType().equals(Poll.class)){
            Poll poll = (Poll) wrappedData.getData();
            poll.printPoll();
        }

        System.out.println("received other than Poll!");
        System.out.println(wrappedData.toString());
    }

    @Override
    public void run() {
        while(running){
            try {
                handleInput(dataStreamManager.readNextInput());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
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
