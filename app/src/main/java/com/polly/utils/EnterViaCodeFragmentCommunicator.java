package com.polly.utils;

import android.widget.Toast;

import com.polly.utils.command.ErrorCommand;
import com.polly.utils.communicator.Communicator;

public class EnterViaCodeFragmentCommunicator extends Communicator {

    public EnterViaCodeFragmentCommunicator(){
        super();
        /**
        new Thread(() -> {
            while(true) {
                try {
                    handleInput(getInput());
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
         */
    }

    @Override
    protected void handleInput(Message message) {
        System.out.println("received input handler! type: " + message.getDataType());
    }
}
