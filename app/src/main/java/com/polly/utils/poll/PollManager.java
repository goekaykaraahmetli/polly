package com.polly.utils.poll;

import com.polly.utils.Message;
import com.polly.utils.communicator.ResponseCommunicator;

import java.time.LocalDateTime;

public class PollManager {
    private static ResponseCommunicator communicator = initialiseCommunicator();

    //public static Poll createPublicPoll(User creator, String name, PollDescription description, LocalDateTime expirationTime, List<String> option)





    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("PollManager received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

                for(Long l : communicator.responseIds){
                    System.out.println(l);
                }

                // no default input handling
            }
        };
    }
}