package com.polly.visuals;

import android.widget.LinearLayout;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class testViewModel extends ViewModel {
    private final MutableLiveData<String> newPoll = new MutableLiveData<String>();
    public void setNewPoll(String name){
        newPoll.setValue(name);
    }
    public LiveData<String> getPoll() {
        return newPoll;
    }
}
