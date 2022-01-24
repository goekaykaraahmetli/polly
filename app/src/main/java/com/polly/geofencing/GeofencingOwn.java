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

public class GeofencingOwn extends ContextWrapper {
    private List<GeofenceEntry> geofences;

    public GeofencingOwn(Context base) {
        super(base);
        geofences = new LinkedList<>();

        geofencing();
    }

    private class GeofenceEntry {
        private Area area;
        private boolean inArea;

        private GeofenceEntry(Area area) {
            this.area = area;
            this.inArea = false;
        }

        private void setInArea(boolean in) {
            this.inArea = in;
        }

        private boolean getInArea() {
            return inArea;
        }

        private Area getArea() {
            return area;
        }
    }

    public void addNewGeofence(Area area) {
        geofences.add(new GeofenceEntry(area));
    }

    public void removeGeofence(Area area) {
        for(GeofenceEntry entry : geofences) {
            if(entry.getArea().equals(area))
                geofences.remove(entry);
        }
    }






    private void geofencing() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                com.polly.utils.Location loc = new com.polly.utils.Location(location.getLatitude(), location.getLongitude());
                for(GeofenceEntry entry : geofences) {
                    //if()
                }

                /*for(Area geofence : geofences) {
                    if(loc.inArea(geofence)) {
                        System.out.println("new geofence (own)");
                    }
                }*/
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
}
