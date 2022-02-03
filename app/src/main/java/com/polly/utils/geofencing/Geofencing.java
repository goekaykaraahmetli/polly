package com.polly.utils.geofencing;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.HandlerThread;
import android.os.IBinder;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.polly.R;
import com.polly.utils.Area;
import com.polly.utils.Organizer;
import com.polly.utils.command.GetGeofencesCommand;
import com.polly.utils.communication.DataStreamManager;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.poll.PollManager;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.GeofenceEntryListWrapper;
import com.polly.utils.wrapper.Message;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Geofencing extends Service {
    private static final String CHANNEL_ID = "channel_gjkasFLjkgjaksfajslfghasf";
    private static final long MINIMUM_DISTANCE_CHANGE_FOR_UPDATES = 0;
    private static final long MINIMUM_TIME_BETWEEN_UPDATES = 10;
    private static final int GEOFENCE_UPDATE_DELAY = 10;
    protected LocationManager locationManager;
    private LocationListener locationListener;
    private ResponseCommunicator communicator;
    private List<GeofenceEntry> geofences;
    Timer timer;
    boolean readyForNewLocation;
    boolean requestingGeofences;


    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        geofences = new LinkedList<>();
        this.communicator = getResponseCommunicator();

        timer = new Timer();
        readyForNewLocation = true;

        initNotificationChannel("Polly Geofence Channel", NotificationManager.IMPORTANCE_DEFAULT, "this is the channel for polly geofencing notifications");

        requestingGeofences = true;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.O)
            startGeofenceForeground();
        else
            startForeground(1, new Notification());
    }
    @RequiresApi(Build.VERSION_CODES.O)
    private void startGeofenceForeground(){
        String NOTIFICATION_CHANNEL_ID = "example.permanence";
        String channelName = "Background Service";
        NotificationChannel chan = new NotificationChannel(NOTIFICATION_CHANNEL_ID, channelName, NotificationManager.IMPORTANCE_NONE);
        chan.setLightColor(Color.BLUE);
        chan.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        assert manager != null;
        manager.createNotificationChannel(chan);

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID);
        Notification notification = notificationBuilder.setOngoing(true)
                .setContentTitle("App is running in background")
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .build();
        startForeground(2, notification);
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int returnVal = super.onStartCommand(intent, flags, startId);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return returnVal;
        }

        HandlerThread handlerThread = new HandlerThread("GeofencingThread");
        handlerThread.start();

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                if(!Organizer.isLoggedIn())
                    return;

                if(requestingGeofences) {
                    try {
                        Message message = communicator.sendWithResponse(DataStreamManager.PARTNERS_DEFAULT_COMMUNICATION_ID, new GetGeofencesCommand(new com.polly.utils.Location(location.getLatitude(), location.getLongitude())));
                        if(message.getDataType() == GeofenceEntryListWrapper.class) {
                            updateGeofences(((GeofenceEntryListWrapper) message.getData()).getGeofenceEntries());
                            requestingGeofences = false;
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    requestingGeofences = true;
                                }
                            }, GEOFENCE_UPDATE_DELAY * 1000);
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }



                if(!readyForNewLocation)
                    return;

                readyForNewLocation = false;

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        readyForNewLocation = true;
                    }
                }, 10000);
                checkTransition(location.getLatitude(), location.getLongitude());
            }
        };

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        locationManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                MINIMUM_TIME_BETWEEN_UPDATES,
                MINIMUM_DISTANCE_CHANGE_FOR_UPDATES,
                locationListener, handlerThread.getLooper());

        return returnVal;
    }



    @Override
    public void onDestroy() {
        super.onDestroy();

        timer.cancel();
        timer = null;
        locationManager.removeUpdates(locationListener);
        communicator = null;

        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
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
        try {
            LocalDateTime expirationTime = PollManager.getPollOptions(entry.getId()).getBasicPollInformation().getExpirationTime();

            if (expirationTime.isBefore(LocalDateTime.now(ZoneId.of("Europe/Berlin")))) {
                removeGeofence(entry);
                return;
            }


            sendNotification("New Poll " + entry.getId() + "available!", "you entered the following area: " + entry.getArea().getLatitude() + "" + entry.getArea().getLongitude() + "" + entry.getArea().getRadius());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void transitionDwell(GeofenceEntry entry) {
        //sendNotification("Dwelling! " + entry.getId(), "you entered the following area: " + entry.getArea().getLatitude() + "" + entry.getArea().getLongitude() + "" + entry.getArea().getRadius());
    }

    private void transitionExit(GeofenceEntry entry) {
        //sendNotification("Exiting! " + entry.getId(), "you entered the following area: " + entry.getArea().getLatitude() + "" + entry.getArea().getLongitude() + "" + entry.getArea().getRadius());
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

    private void initNotificationChannel(String name, int importance, String description) {
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);

        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        notificationManager.createNotificationChannel(channel);
    }

    public void removeGeofence(GeofenceEntry entry) {
        geofences.remove(entry);
    }

    private void updateGeofences(List<GeofenceEntry> geofenceEntries) {
        List<GeofenceEntry> newGeofenceEntries = new LinkedList<>();
        List<GeofenceEntry> oldGeofenceEntries = new LinkedList<>();

        for(GeofenceEntry newEntry : geofenceEntries) {
            GeofenceEntry isInList = null;
            for(GeofenceEntry oldEntry : geofences) {
                if(newEntry.getId() == oldEntry.getId())
                    isInList = oldEntry;
            }

            if(isInList == null) {
                newGeofenceEntries.add(newEntry);
            } else {
                oldGeofenceEntries.add(isInList);
            }
        }

        geofences.removeAll(oldGeofenceEntries);
        geofences.addAll(newGeofenceEntries);
    }
}
