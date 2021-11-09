package com.polly.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class InputReceiverManager {
    private static long nextId = 0L;
    private static Map<Long, InputReceiver> inputReceivers = new HashMap<>();

    public static long getNextId(){
        return nextId++;
    }

    public static void registerInputReceiver(InputReceiver inputReceiver){
        inputReceivers.put(inputReceiver.getId(), inputReceiver);
    }

    public static InputReceiver getInputReceiverById(long id) throws NoSuchElementException {
        if(!inputReceivers.containsKey(id))
            throw new NoSuchElementException();

        return inputReceivers.get(id);
    }
}
