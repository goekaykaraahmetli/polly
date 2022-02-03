package com.polly.visuals;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.polly.R;
import com.polly.utils.Organizer;
import com.polly.utils.SavingClass;
import com.polly.utils.geofencing.Geofencing;
import com.polly.utils.geofencing.Geofencing2;
import com.polly.utils.geofencing.Geofencing3;
import com.polly.utils.geofencing.Restarter;

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    Intent geofencingIntent;
    private Geofencing2 geofencingService;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Polly);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SavingClass saving = new ViewModelProvider(this).get(SavingClass.class);



        new Organizer(this);

        geofencingService = new Geofencing2();
        geofencingIntent = new Intent(this, geofencingService.getClass());
        if(!isMyServiceRunning(geofencingService.getClass()))
            startService(geofencingIntent);

    }
    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, Restarter.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.nav_account:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.accountFragment);
                break;
            case R.id.nav_startpoll:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
                break;
            case R.id.nav_enterpoll:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.enterpollFragment);
                break;
            case R.id.nav_recentpolls:
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.recentFragment);
                break;
            case R.id.nav_groups:
                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                    Toast.makeText(this, "Please sign in first", Toast.LENGTH_SHORT).show();
                else
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.mainActivityChat);
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        actionBarDrawerToggle.onOptionsItemSelected(item);
        return super.onOptionsItemSelected(item);
    }
}
