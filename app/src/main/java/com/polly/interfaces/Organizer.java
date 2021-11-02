package com.polly.interfaces;

import com.polly.interfaces.communication.SocketHandler;

import java.io.IOException;

public class Organizer {
    private static final SocketHandler socketHandler;
    static {
        socketHandler = createSocketHandler();

    }

    private static SocketHandler createSocketHandler(){
        SocketHandler sh = null;
        try {
            sh = new SocketHandler("91.42.63.185", 1337);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return sh;
    }


    public static SocketHandler getSocketHandler(){
        return socketHandler;
    }
}
