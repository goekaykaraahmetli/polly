package com.polly.utils.user;

import com.polly.config.Config;
import com.polly.utils.command.user.FindUsersCommand;
import com.polly.utils.command.user.GetMyUsergroupsCommand;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;
import com.polly.utils.wrapper.UserListWrapper;
import com.polly.utils.wrapper.UserWrapper;
import com.polly.utils.wrapper.UsergroupListWrapper;
import com.polly.utils.wrapper.UsergroupWrapper;

import java.io.IOException;
import java.util.List;

public class UserManager {
    private static ResponseCommunicator communicator = initialiseCommunicator();
    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("UserManager received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

                for(Long l : communicator.responseIds){
                    System.out.println(l);
                }

                // no default input handling
            }
        };
    }

    public static List<UsergroupWrapper> getMyUsergroups() throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new GetMyUsergroupsCommand());
        if(response.getDataType() == UsergroupListWrapper.class)
            return ((UsergroupListWrapper) response.getData()).getUsergroupList();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }

    public static List<UserWrapper> findUsers() throws IOException {
        Message response = communicator.sendWithResponse(Config.serverCommunicationId, new FindUsersCommand());
        if(response.getDataType() == UserListWrapper.class)
            return ((UserListWrapper) response.getData()).getUserList();
        if(response.getDataType() == ErrorWrapper.class)
            throw new IOException(((ErrorWrapper) response.getData()).getMessage());
        throw new IOException("Something went wrong!");
    }
}