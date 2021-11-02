package com.polly.interfaces.communication;

import java.io.IOException;

class OutputHandler {
    private DataStreamManager dataStreamManager;

    public OutputHandler(DataStreamManager dataStreamManager){
        this.dataStreamManager = dataStreamManager;
    }

    public void writeOutput(WrappedData wrappedData){
        new Thread(() -> {
            try {
                dataStreamManager.writeOutput(wrappedData);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }

}
