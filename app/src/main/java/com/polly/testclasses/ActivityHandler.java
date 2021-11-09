package com.polly.testclasses;

import com.polly.MainActivity;

public class ActivityHandler {
    private static MainActivity mainActivity;

    public static void setMainActivity(MainActivity newMainActivity){
        mainActivity = newMainActivity;
    }

    public static MainActivity getMainActivity(){
        return mainActivity;
    }
}