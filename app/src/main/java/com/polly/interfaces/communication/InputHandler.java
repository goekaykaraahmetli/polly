package com.polly.interfaces.communication;

import com.polly.testclasses.ActivityHandler;
import com.polly.testclasses.Poll;

import java.io.IOException;

class InputHandler extends Thread{
    private boolean running = false;
    private final DataStreamManager dataStreamManager;

    public InputHandler(DataStreamManager dataStreamManager){
        this.dataStreamManager = dataStreamManager;
    }

    private void handleInput(long id, WrappedData wrappedData){
        // redirecting to the InputReceiver that is in charge of the input:
        //InputReceiver receiver = InputReceiverManager.getInputReceiverById(id);
        //receiver.addInput(wrappedData);

        //TODO handle input
        if(wrappedData.getDataType().equals(Poll.class)){
            Poll poll = (Poll) wrappedData.getData();
            //ActivityHandler.getMainActivity().testPoll(poll);
            return;

            //poll.printPoll();
        }

        System.out.println("received other than Poll!");
        System.out.println(wrappedData.toString());
    }

    private void handleInput(WrappedData data){
        // TODO
        // deprecated!
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
