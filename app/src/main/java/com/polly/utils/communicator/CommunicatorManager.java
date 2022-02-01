package com.polly.utils.communicator;

import com.polly.config.Config;
import com.polly.utils.Organizer;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

import kotlin.random.Random;

public class CommunicatorManager {
    private static long nextId = 0L;
    private static Map<Long, Communicator> communicators = new HashMap<>();
    private static DefaultCommunicator defaultCommunicator;

    static {
        defaultCommunicator = new DefaultCommunicator();
    }

    private CommunicatorManager() {
        super();
    }

    public static long getNextId(){
        System.out.println("next id: " + nextId);
        return nextId++;
    }

    public static void registerCommunicator(Communicator communicator){
        communicators.put(communicator.getCommunicationId(), communicator);
    }

    public static Communicator getCommunicatorById(long id) throws NoSuchElementException {
        if(!communicators.containsKey(id))
            throw new NoSuchElementException();
        return communicators.get(id);
    }

    public static void timedOut() {
        Organizer.timedOut();
        for(Communicator communicator : communicators.values()) {
            if(communicator instanceof ResponseCommunicator) {
                ResponseCommunicator responseCommunicator = (ResponseCommunicator) communicator;
                responseCommunicator.timedOut();
            }
        }
    }

    public static DefaultCommunicator getDefaultCommunicator() {
        return defaultCommunicator;
    }
}