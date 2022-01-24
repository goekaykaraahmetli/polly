package com.polly.geofencing;

import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;

import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.maps.model.LatLng;

public class GeofenceHelper extends ContextWrapper {
    private static long nextGeofenceRequestId = 0L;
    private static final int REQUEST_CODE = 0;

    private PendingIntent pendingIntent;


    public GeofenceHelper(Context context){
        super(context);
    }

    public GeofencingRequest getGeofencingRequest(Geofence geofence) {
        return new GeofencingRequest.Builder()
                .addGeofence(geofence)
                .setInitialTrigger(GeofencingRequest.INITIAL_TRIGGER_ENTER)
                .build();
    }

    public Geofence getGeofence(LatLng latLng, float radius, int transitionTypes, long duration) {
        return new Geofence.Builder()
                .setCircularRegion(latLng.latitude, latLng.longitude, radius)
                .setRequestId(getNextGeofenceRequestId())
                .setTransitionTypes(transitionTypes)
                .setLoiteringDelay(1000)
                .setExpirationDuration(duration)
                .build();
    }

    public String getNextGeofenceRequestId() {
        return String.valueOf(nextGeofenceRequestId++);
    }

    public PendingIntent getPendingIntent() {
        if(pendingIntent != null)
            return pendingIntent;

        Intent intent = new Intent(this, GeofenceBroadcastReceiver.class);
        pendingIntent = PendingIntent.getBroadcast(this, REQUEST_CODE, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }
}