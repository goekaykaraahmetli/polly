package com.polly.visuals;
import com.polly.utils.poll.PollManager;


import android.content.DialogInterface;
import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.polly.R;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePollFragment extends Fragment {
    int optionCounter = 0;
    int optionMax = 8;
    boolean start = true;
    boolean isSaved = true;
    int numberOfParticipants;
    int numberOfOptions;
    HashMap<Integer, EditText> map = new HashMap<>();
    HashMap<Integer, Button> remove = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_create_poll, container, false);
        SavingClass saving = new ViewModelProvider(getActivity()).get(SavingClass.class);
        if(saving.getDropDownMenu().toString().equals("POLLYROOM")){
            optionMax = 4;
        }
        Button createPollBtn = (Button) root.findViewById(R.id.createPollBtn);
        TextView pollName = (TextView) root.findViewById(R.id.PollName);
        pollName.setText(saving.getPollname());
        if(saving.getMap() != null){
            map = saving.getMap();
        }
        EditText option1 = new EditText(getContext());
        option1.setHint("Option " + (++optionCounter));
        Button remove1 = new Button(getContext());
        remove1.setText("remove Option");
        remove1.setVisibility(View.INVISIBLE);
        ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(option1);
        ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(remove1);
        EditText option2 = new EditText(getContext());
        option2.setHint("Option " + (++optionCounter));
        ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(option2);
        Button remove2 = new Button(getContext());
        remove2.setText("remove Option");
        remove2.setVisibility(View.INVISIBLE);
        ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(remove2);

            map.put(0, option1);
            map.put(1, option2);
            remove.put(0, remove1);
            remove.put(1, remove2);
        if(saving.getOptionCounter()>=2){
            optionCounter = saving.getOptionCounter();
            start = saving.isStart();
            option1.setText(saving.getPollOptions().get(0));
            option2.setText(saving.getPollOptions().get(1));
            remove1.setVisibility(View.VISIBLE);
            remove2.setVisibility(View.VISIBLE);
            if(optionCounter > optionMax){
                optionCounter = 4;
            }
            for(int i = 2; i< optionCounter; i++){
                EditText newOption = new EditText(getContext());
                newOption.setHint("Option " + i);
                newOption.setText(map.get(i).getText());
                ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(newOption);
                Button delete = new Button(getContext());
                delete.setText("remove Option");
                ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(delete);
                remove.put(i, delete);
                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(root.findViewById(R.id.addOption).getVisibility() == View.INVISIBLE){
                            root.findViewById(R.id.addOption).setVisibility(View.VISIBLE);
                        }
                        newOption.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                        int id = getKey(remove, delete);

                        for (int i = id; i < optionCounter; i++) {
                            if (map.containsKey(i + 1)) {
                                map.get(i + 1).setHint("Option " + (i+1));
                                map.replace(i, map.get(i + 1));
                                remove.replace(i, remove.get(i + 1));
                            }else{
                                map.remove(i);
                                remove.remove(i);
                            }
                        }
                        optionCounter--;

                        if(optionCounter < 3){
                            remove.get(0).setVisibility(View.INVISIBLE);
                            remove.get(1).setVisibility(View.INVISIBLE);

                            if(option1.getVisibility() != View.GONE){
                                remove1.setVisibility(View.INVISIBLE);
                            }
                            if(option2.getVisibility() != View.GONE){
                                remove2.setVisibility(View.INVISIBLE);
                            }
                            start = true;
                        }
                    }
                });
            }
        }


        root.findViewById(R.id.goBack).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!saving.isSaved()) {
                    androidx.appcompat.app.AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                    alert.setTitle("Options not saved");
                    alert.setMessage("Save before exit?");
                    alert.setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saving.setSaved(true);
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
                        }
                    });
                    alert.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            saving.setSaved(true);
                            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
                        }
                    });
                    alert.create().show();
                }
            }
        });
        createPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Editable poll = pollName.getText();
                //CharSequence poll1 = poll.toString();
                List<String> pollOptions = new ArrayList<>();
                boolean hinted = false;
                for (int i = 0; i < optionCounter; i++) {
                    if(map.get(i).getText().length() == 0){
                        hinted = true;
                        break;
                    }
                    pollOptions.add(map.get(i).getText().toString());
                }
                if(!hinted) {
                    /*try {
                        long id = PollManager.createPoll(poll.toString(), pollOptions);
                        Toast.makeText(getActivity(), "Poll ID is: " + id, Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException | IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), "No connection to the server!", Toast.LENGTH_SHORT).show();
                    }*/
                    Toast.makeText(getActivity(), "Options saved", Toast.LENGTH_SHORT).show();
                    saving.setPollOptions(pollOptions);
                    saving.setOptionCounter(optionCounter);
                    saving.setRemove(remove);
                    saving.setMap(map);
                    saving.setStart(start);
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.polloptionFragment);
                }else{
                    Toast.makeText(getActivity(), "Please edit all Options", Toast.LENGTH_SHORT).show();
                }

            }
        });




        Button addOption = (Button) root.findViewById(R.id.addOption);

        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(optionCounter >= optionMax) {
                    return;
                }
                saving.setSaved(false);
                isSaved = false;
                System.out.println(saving.getDropDownMenu().toString());
                EditText newOption = new EditText(getContext());
                newOption.setHint("Option " + (optionCounter+1));

                ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(newOption);
                map.put(optionCounter, newOption);

                Button delete = new Button(getContext());
                delete.setText("remove Option");
                ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(delete);
                remove.put(optionCounter, delete);
                if(start){
                    if(option1.getVisibility() != View.GONE)
                        remove1.setVisibility(View.VISIBLE);
                    if(option2.getVisibility() != View.GONE)
                        remove2.setVisibility(View.VISIBLE);
                        start = false;
                }

                optionCounter++;
                if(optionCounter > 2){
                    remove.get(0).setVisibility(View.VISIBLE);
                    remove.get(1).setVisibility(View.VISIBLE);
                }
                if(optionCounter == optionMax){
                    root.findViewById(R.id.addOption).setVisibility(View.INVISIBLE);
                }

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(root.findViewById(R.id.addOption).getVisibility() == View.INVISIBLE){
                            root.findViewById(R.id.addOption).setVisibility(View.VISIBLE);
                        }
                        newOption.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                        int id = getKey(remove, delete);

                        for (int i = id; i < optionCounter; i++) {
                            if (map.containsKey(i + 1)) {
                                map.get(i + 1).setHint("Option " + (i+1));
                                map.replace(i, map.get(i + 1));
                                remove.replace(i, remove.get(i + 1));
                            }else{
                                map.remove(i);
                                remove.remove(i);
                            }
                        }
                        optionCounter--;

                        if(optionCounter < 3){
                            remove.get(0).setVisibility(View.INVISIBLE);
                            remove.get(1).setVisibility(View.INVISIBLE);

                            if(option1.getVisibility() != View.GONE){
                                remove1.setVisibility(View.INVISIBLE);
                            }
                            if(option2.getVisibility() != View.GONE){
                                remove2.setVisibility(View.INVISIBLE);
                            }
                            start = true;
                        }
                    }
                });
            }

        });


        remove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                option1.setVisibility(View.GONE);
                remove1.setVisibility(View.GONE);


                HashMap<Integer, EditText> tmp = map;

                for(int i = 0; i< optionCounter; i++) {
                    if (map.containsKey(i + 1)) {
                        map.get(i + 1).setHint("Option " + (i+1));
                        map.replace(i, map.get(i + 1));
                        remove.replace(i, remove.get(i + 1));
                    }else{
                        map.remove(i);
                        remove.remove(i);
                    }
                }
                optionCounter--;
                if(optionCounter < 3){
                    remove.get(0).setVisibility(View.INVISIBLE);
                    remove.get(1).setVisibility(View.INVISIBLE);

                    if(option2.getVisibility() != View.GONE){
                        remove2.setVisibility(View.INVISIBLE);
                    }
                    start = true;
                }
            }
        });


        remove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                option2.setVisibility(View.GONE);
                remove2.setVisibility(View.GONE);

                int id = getKey(remove, remove2);


                for(int i = id; i< optionCounter; i++){
                    if(map.containsKey(i+1)) {
                        map.get(i + 1).setHint("Option " + (i+1));
                        map.replace(i, map.get(i + 1));
                        remove.replace(i, remove.get(i + 1));
                    }
                    else{
                        map.remove(i);
                        remove.remove(i);
                    }
                }

                optionCounter--;
                if(optionCounter < 3){
                    remove.get(0).setVisibility(View.INVISIBLE);
                    remove.get(1).setVisibility(View.INVISIBLE);

                    if(option1.getVisibility() != View.GONE){
                        remove1.setVisibility(View.INVISIBLE);
                    }
                    start = true;
                }

            }
        });

        return root;
    }
    public <Integer, Button>  Integer getKey(HashMap<Integer, Button> map, Button b) {
        for (Map.Entry<Integer, Button> entry : map.entrySet()) {
            if (entry.getValue().equals(b)) {
                return entry.getKey();
            }
        }
        return null;
    }

}