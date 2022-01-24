package com.polly.geofencing;

import android.Manifest;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.polly.utils.Area;

import java.util.LinkedList;
import java.util.List;

public class Geofencing extends ContextWrapper {
    private List<GeofenceEntry> geofences;

    public Geofencing(Context base) {
        super(base);
        geofences = new LinkedList<>();

        start();
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





        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 10, locationListener);
    }

    private void checkTransition(double latitude, double longitude) {
        com.polly.utils.Location loc = new com.polly.utils.Location(latitude, longitude);
        for(GeofenceEntry entry : geofences) {
            Area area = entry.getArea();
            boolean inArea = entry.getInArea();

            if(loc.inArea(area) && inArea == false)
                transitionEnter(area);

            else if(loc.inArea(area) && inArea == true)
                transitionDwell(area);

            else if(!loc.inArea(area) && inArea == true)
                transitionExit(area);

            entry.setInArea(loc.inArea(area));
        }
    }


    private void transitionEnter(Area area) {

    }

    private void transitionDwell(Area area) {

    }

    private void transitionExit(Area area) {

    }
}
