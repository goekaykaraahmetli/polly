package com.polly.interfaces.communication;

import java.io.IOException;

public class InputHandler extends Thread{
    private boolean running = false;
    private DataStreamManager dataStreamManager;

    public InputHandler(DataStreamManager dataStreamManager){
        this.dataStreamManager = dataStreamManager;
    }

    private void handleInput(WrappedData wrappedData){
        //TODO handle input
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
