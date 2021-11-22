package com.polly.visuals;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class accountViewModel extends ViewModel {
    private final MutableLiveData<String> emailData = new MutableLiveData<String>();
    private final MutableLiveData<String> usernameData = new MutableLiveData<String>();
    public void setEmail(String email){
        emailData.setValue(email);
    }
    public String getEmail() {
        return emailData.getValue();
    }

    public void setUsername(String username){
        usernameData.setValue(username);
    }
    public String getUsername() {
        return usernameData.getValue();
    }
}
