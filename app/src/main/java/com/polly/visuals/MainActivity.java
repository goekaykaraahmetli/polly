package com.polly.visuals;

import android.os.Bundle;
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

import java.io.IOException;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Polly);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        SavingClass saving = new ViewModelProvider(this).get(SavingClass.class);





    new Organizer();

        try {
            Organizer.send(0L, 0L, "connected!");
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }

    }




    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
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
            case R.id.nav_signin_signup:
                if(FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.loginFragment);
                }
                else
                    Toast.makeText(this, "You are already signed in. You can log out through the account page", Toast.LENGTH_SHORT).show();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        return true;
    }

}
