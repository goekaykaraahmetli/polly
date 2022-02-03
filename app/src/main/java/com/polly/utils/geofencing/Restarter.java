package com.polly.utils.geofencing;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

public class Restarter extends BroadcastReceiver {
    private static boolean running;

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!running)
            return;


        Log.i("Broadcast Listened", "Service tried to stop");
        Toast.makeText(context, "Service restarted", Toast.LENGTH_SHORT).show();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            context.startForegroundService(new Intent(context, Geofencing.class));
        } else {
            context.startService(new Intent(context, Geofencing.class));
        }
    }

    public static void stop() {
        running = false;
    }

    public static void start() {
        running = true;
    }
}