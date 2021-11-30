package com.polly.visuals;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.polly.R;

public class LoginActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);


    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.nav_account:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new AccountFragment()).commit();
                break;
            case R.id.nav_startpoll:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new StartpollFragment()).commit();
                break;
            case R.id.nav_enterpoll:
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new EnterpollFragment()).commit();
                break;
            case R.id.nav_recentpolls:
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentFragment()).commit();
                break;
        }

        return true;
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case R.id.menu_main_settings:
                // TODO
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new SettingsFragment()).commit();
                return true;
            case R.id.menu_main_login_or_sign_up:
                //TODO
                if(FirebaseAuth.getInstance().getCurrentUser() == null)
                getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainerView, new LoginFragment()).commit();
                else
                    Toast.makeText(this, "You are already signed in. You can sign out through the account page", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
