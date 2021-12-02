package com.polly.visuals;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;
import androidx.navigation.Navigation;

import com.budiyev.android.codescanner.CodeScanner;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.config.Config;
import com.polly.utils.Organizer;
import com.polly.utils.command.CreatePollCommand;
import com.polly.utils.command.GetParticipatedPollsCommand;
import com.polly.utils.poll.Poll;
import com.polly.utils.poll.PollManager;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    protected DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private String username;




    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);
        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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
                Navigation.findNavController(this, R.id.nav_host_fragment).navigate(R.id.createPollFragment);
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
