package com.polly.geofencing;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.Geofence;
import com.google.android.gms.location.GeofenceStatusCodes;
import com.google.android.gms.location.GeofencingClient;
import com.google.android.gms.location.GeofencingRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

public class Geofencing extends ContextWrapper {
    GeofencingClient geofencingClient;
    GeofenceHelper geofenceHelper;


    public Geofencing(Context context) {
        super(context);

        geofencingClient = LocationServices.getGeofencingClient(this);
        geofenceHelper = new GeofenceHelper(this);

        test();
    }

    private void test() {
        locationListener();



        LatLng latLng = new LatLng(50.0027, 9.2252);
        addGeofence(latLng, 3000.0F, geofencingClient, geofenceHelper, Geofence.NEVER_EXPIRE);
    }



    private void addGeofence(LatLng latLng, float radius, GeofencingClient geofencingClient, GeofenceHelper geofenceHelper, long expirationTime) {
        Geofence geofence = geofenceHelper.getGeofence(latLng, radius, Geofence.GEOFENCE_TRANSITION_ENTER, expirationTime);
        GeofencingRequest req = geofenceHelper.getGeofencingRequest(geofence);
        PendingIntent penInt = geofenceHelper.getPendingIntent();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_BACKGROUND_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        geofencingClient.addGeofences(req, penInt).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                System.out.println("created Geofence");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                ApiException apiException = (ApiException) e;
                switch (apiException.getStatusCode()) {
                    case GeofenceStatusCodes
                            .GEOFENCE_NOT_AVAILABLE:
                        System.out.println("GEOFENCE_NOT_AVAILABLE");
                        break;
                    case GeofenceStatusCodes
                            .GEOFENCE_TOO_MANY_GEOFENCES:
                        System.out.println("GEOFENCE_TOO_MANY_GEOFENCES");
                        break;
                    case GeofenceStatusCodes
                            .GEOFENCE_TOO_MANY_PENDING_INTENTS:
                        System.out.println("GEOFENCE_TOO_MANY_PENDING_INTENTS");
                        break;
                    default:
                        System.out.println("Creating the geofence failed: " + e.getMessage());
                        e.printStackTrace();
                        break;
                }
            }
        });
    }

    private void locationListener() {
        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        LocationListener locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(@NonNull Location location) {
                // do nothing
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
}
