package com.polly.geofencing;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.polly.R;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.wrapper.Message;

import java.util.LinkedList;
import java.util.List;

public class Geofencing extends ContextWrapper {
    private static final String CHANNEL_ID = "channel_gjkasFLjkgjaksf";
    private ResponseCommunicator responseCommunicator;
    private List<GeofenceEntry> geofences;

    public Geofencing(Context base) {
        super(base);
        geofences = new LinkedList<>();
        this.responseCommunicator = getResponseCommunicator();


        initNotificationChannel("Polly Geofence Channel", NotificationManager.IMPORTANCE_DEFAULT, "this is the channel for polly geofencing notifications");

        sendNotification("test", "test");

        start();

        addNewGeofence(new Area(50.0029, 9.2247, 3000));
    }

    private void initNotificationChannel(String name, int importance, String description) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void addNewGeofence(Area area) {
        geofences.add(new GeofenceEntry(area));
    }

    public void removeGeofence(Area area) {
        List<GeofenceEntry> toRemove = new LinkedList<>();

        for(GeofenceEntry entry : geofences) {
            if(entry.getArea().equals(area))
                toRemove.add(entry);
        }

        geofences.removeAll(toRemove);
    }


    private void start() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                System.out.println("location changed-------------------------------------------------------------");
                checkTransition(location.getLatitude(), location.getLongitude());
            }
        };

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            return;
        }


        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
    }

    private void checkTransition(double latitude, double longitude) {
        com.polly.utils.Location loc = new com.polly.utils.Location(latitude, longitude);
        for(GeofenceEntry entry : geofences) {
            Area area = entry.getArea();
            boolean inArea = entry.getInArea();
            boolean newInArea = loc.inArea(area);

            entry.setInArea(newInArea);

            if(newInArea && inArea == false)
                transitionEnter(area);

            else if(newInArea && inArea == true)
                transitionDwell(area);

            else if(!newInArea && inArea == true)
                transitionExit(area);
        }
    }


    private void transitionEnter(Area area) {
        sendNotification("New Poll available!", "you entered the following area: " + area.getLatitude() + "" + area.getLongitude() + "" + area.getRadius());
    }

    private void transitionDwell(Area area) {
        sendNotification("Dwelling!", "you entered the following area: " + area.getLatitude() + "" + area.getLongitude() + "" + area.getRadius());
    }

    private void transitionExit(Area area) {
        sendNotification("Exiting!", "you entered the following area: " + area.getLatitude() + "" + area.getLongitude() + "" + area.getRadius());
    }



    private void sendNotification(String title, String content) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_logo)
                .setContentTitle(title)
                .setContentText(content)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(this);
        notificationManagerCompat.notify(0, builder.build());
    }

    private ResponseCommunicator getResponseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("AccountFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());
            }
        };
    }
}
