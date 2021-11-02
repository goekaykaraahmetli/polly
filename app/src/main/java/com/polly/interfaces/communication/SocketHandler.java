package com.polly.interfaces.communication;

import java.io.IOException;
import java.net.Socket;
import java.util.concurrent.atomic.AtomicBoolean;

public class SocketHandler {
    private Socket socket;
    private InputHandler inputHandler;
    private OutputHandler outputHandler;

    public SocketHandler(String ip, int port) throws IOException {
        connect(ip, port);
    }

    public <T> void writeOutput(T data){
        WrappedData wrappedData = new WrappedData(data.getClass(), data);
        outputHandler.writeOutput(wrappedData);
    }


    public void close(){
        new Thread(() -> {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public void connect(String ip, int port) throws IOException{
        AtomicBoolean connecting = new AtomicBoolean(true);
        new Thread(() -> {
            try {
                socket = new Socket(ip, port);
            } catch (IOException e) {
                e.printStackTrace();
                try{
                    socket.close();
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
            }
            connecting.set(false);
        }).start();

        while(connecting.get()){}

        if(socket == null){
            throw new IOException("could not connect to the given server!");
        }

        DataStreamManager dataStreamManager = new DataStreamManager(socket.getInputStream(), socket.getOutputStream());

        inputHandler = new InputHandler(dataStreamManager);
        outputHandler = new OutputHandler(dataStreamManager);
    }
}
