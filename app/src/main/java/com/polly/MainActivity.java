package com.polly;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.android.material.navigation.NavigationView;
import com.google.zxing.Result;
import com.polly.interfaces.Organizer;
import com.polly.utils.Poll;
import com.polly.utils.commands.Command;
import com.polly.utils.commands.CommandCreator;
import com.polly.visuals.AccountFragment;
import com.polly.visuals.EnterpollFragment;
import com.polly.visuals.PollActivity;
import com.polly.visuals.RecentFragment;
import com.polly.visuals.StartpollFragment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle actionBarDrawerToggle;
    private CodeScanner mCodeScanner;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);




        //CommandCreator.createPollCommand(CommandCreator.PollCommandActions.CREATE, "testPoll");
        List<String> pollOptions = new ArrayList<>();
        pollOptions.add("Fichte");
        pollOptions.add("Buche");
        pollOptions.add("Eiche");
        pollOptions.add("Esche");
        String[] pollOptionsArray = listToArray(pollOptions);

        Command command = CommandCreator.createPollCommand(CommandCreator.PollCommandActions.CREATE, "Bäume", pollOptions);

        List<String> fichte = new ArrayList<>();
        fichte.add("Fichte");
        Command command2 = CommandCreator.createPollCommand(CommandCreator.PollCommandActions.VOTE, "Bäume", "Fichte");

        Command command3 = CommandCreator.createPollCommand(CommandCreator.PollCommandActions.LOAD, "Bäume");
/**
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
                    Thread.sleep(500);
                    Organizer.getSocketHandler().writeOutput(new Integer(123));
                    Thread.sleep(500);
                    Organizer.getSocketHandler().writeOutput(command);
                    Thread.sleep(500);
                    Organizer.getSocketHandler().writeOutput(command2);
                    Thread.sleep(500);
                    Organizer.getSocketHandler().writeOutput(command3);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
**/
        drawerLayout = findViewById(R.id.my_drawer_layout);
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.nav_open, R.string.nav_close);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();


        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);


        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



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
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RecentFragment()).commit();
                break;
        }

        drawerLayout.closeDrawer(GravityCompat.START);
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
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;
        switch (id) {
            case R.id.menu_main_settings:
                // TODO
                //getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SettingsFragment()).commit();
                openDemoPoll();
                break;
            case R.id.menu_main_login_or_sign_up:
                //TODO
                Intent intent = new Intent(this, LoginActivity.class);
                startActivity(intent);
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }


    private void openDemoPoll(){
        Map<String, Integer> map = new HashMap<>();
        map.put("Fichte", 10);
        map.put("Buche", 12);
        map.put("Eiche", 24);
        Poll.setCurrentPoll(new Poll("Bäume",map));
        Intent intent = new Intent(this, PollActivity.class);
        startActivity(intent);
    }


    private String[] listToArray(List<String> list){
        String[] tArray = new String[list.size()];
        for(int i = 0;i< tArray.length;i++){
            tArray[i] = list.get(i);
        }
        return tArray;
    }
}
