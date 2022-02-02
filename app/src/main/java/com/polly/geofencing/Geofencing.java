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
import com.polly.utils.Area;
import com.polly.utils.Organizer;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.wrapper.GeofenceEntryListWrapper;
import com.polly.utils.wrapper.Message;

import java.io.IOException;
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
        start();

        addNewGeofence(new Area(50.0029, 9.2247, 3000), 1);
    }

    private void initNotificationChannel(String name, int importance, String description) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void addNewGeofence(Area area, long id) {
        geofences.add(new GeofenceEntry(area, id));
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
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        checkTransition(location.getLatitude(), location.getLongitude());
                    }
                }).start();
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
                transitionEnter(entry);

            else if(newInArea && inArea == true)
                transitionDwell(entry);

            else if(!newInArea && inArea == true)
                transitionExit(entry);
        }
    }


    private void transitionEnter(GeofenceEntry entry) {
        sendNotification("New Poll " + entry.getId() + "available!", "you entered the following area: " + entry.getArea().getLatitude() + "" + entry.getArea().getLongitude() + "" + entry.getArea().getRadius());
    }

    private void transitionDwell(GeofenceEntry entry) {
        sendNotification("Dwelling! " + entry.getId(), "you entered the following area: " + entry.getArea().getLatitude() + "" + entry.getArea().getLongitude() + "" + entry.getArea().getRadius());
    }

    private void transitionExit(GeofenceEntry entry) {
        sendNotification("Exiting! " + entry.getId(), "you entered the following area: " + entry.getArea().getLatitude() + "" + entry.getArea().getLongitude() + "" + entry.getArea().getRadius());
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

                if(message.getDataType() == GeofenceEntryListWrapper.class)
                    geofences = ((GeofenceEntryListWrapper) message.getData()).getGeofenceEntries();
            }
        };
    }
}