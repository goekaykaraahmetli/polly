package com.polly.utils.communicator;

import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;

public class CommunicatorManager {
    private static long nextId = 0L;
    private static Map<Long, Communicator> communicators = new HashMap<>();
    
    static {
    	new DefaultCommunicator();
    }
    
    private CommunicatorManager() {
    	super();
    }
    
    public static long getNextId(){
        return nextId++;
    }

    public static void registerCommunicator(Communicator communicator){
    	communicators.put(communicator.getId(), communicator);
    }

	public static Communicator getCommunicatorById(long id) throws NoSuchElementException {
        if(!communicators.containsKey(id))
            throw new NoSuchElementException();
        return communicators.get(id);
    }
	
	public static Communicator getDefaultCommunicator() {
		return getCommunicatorById(0L);
	}
}