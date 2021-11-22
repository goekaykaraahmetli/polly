package com.polly.visuals;
import com.polly.utils.poll.PollManager;


import android.graphics.Color;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.polly.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CreatePollFragment extends Fragment {
    int optionCounter = 3;
    boolean start = true;
    HashMap<Integer, EditText> map = new HashMap<>();
    HashMap<Integer, Button> remove = new HashMap<>();
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.activity_create_poll, container, false);
        Button createPollBtn = (Button) root.findViewById(R.id.createPollBtn);
        EditText pollName = (EditText) root.findViewById(R.id.PollName);



        createPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable poll = pollName.getText();
                CharSequence poll1 = poll.toString();
                if(poll1.length() < 1){
                    Toast.makeText(getActivity(), "Please enter a Pollname.", Toast.LENGTH_SHORT).show();
                }else {
                    List<String> pollOptions = new ArrayList<>();
                    for (int i = 1; i < optionCounter; i++) {
                        pollOptions.add(map.get(i).getText().toString());
                    }
                    try {
                        long id = PollManager.createPoll(poll.toString(), pollOptions);
                        Toast.makeText(getActivity(), "Poll ID is: " + id, Toast.LENGTH_SHORT).show();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });




        /**
         Button b3 = new Button(getContext());
         b3.setVisibility(View.VISIBLE);
         b3.setText("asd");
         b3.animate();

         RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500, 100);
         params.setMargins(10,10,10,10);
         b3.setLayoutParams(params);

         ((RelativeLayout) root.findViewById(R.id.startpoll_relativeLayout)).addView(b3);
         **/


        map.put(1, (EditText) root.findViewById(R.id.option1));
        map.put(2, (EditText) root.findViewById(R.id.option2));
        remove.put(1, (Button) root.findViewById(R.id.removeBtn1));
        remove.put(2, (Button) root.findViewById(R.id.removeBtn2));
        Button addOption = (Button) root.findViewById(R.id.addOption);

        Button remove1 = (Button) root.findViewById(R.id.removeBtn1);
        Button remove2 = (Button) root.findViewById(R.id.removeBtn2);
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText newOption = new EditText(getContext());
                newOption.setHint("Option " + optionCounter);

                ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(newOption);
                map.put(optionCounter, newOption);

                Button delete = new Button(getContext());
                delete.setText("remove Option");
                delete.setLayoutParams(remove1.getLayoutParams());
                ((LinearLayout) root.findViewById(R.id.linear_layout)).addView(delete);
                remove.put(optionCounter, delete);
                if(start){
                    if(root.findViewById(R.id.option1).getVisibility() != View.GONE)
                        remove1.setVisibility(View.VISIBLE);
                    if(root.findViewById(R.id.option2).getVisibility() != View.GONE)
                        remove2.setVisibility(View.VISIBLE);
                    remove1.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    remove2.setBackgroundColor(Color.parseColor("#CCCCCC"));
                    start = false;
                }

                optionCounter++;

                if(optionCounter > 2){
                    remove.get(1).setVisibility(View.VISIBLE);
                    remove.get(2).setVisibility(View.VISIBLE);
                }

                delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        newOption.setVisibility(View.GONE);
                        delete.setVisibility(View.GONE);
                        int id = getKey(remove, delete);
                        for (int i = id; i < optionCounter; i++) {
                            if (map.containsKey(i + 1)) {
                                map.get(i + 1).setHint("Option " + i);
                                map.replace(i, map.get(i + 1));
                                remove.replace(i, remove.get(i + 1));
                            }
                            optionCounter--;

                            if(optionCounter <= 3){
                                remove.get(1).setVisibility(View.INVISIBLE);
                                remove.get(2).setVisibility(View.INVISIBLE);
                            }

                            if(optionCounter<=3) {
                                if(root.findViewById(R.id.option1).getVisibility() != View.GONE){
                                    remove1.setVisibility(View.INVISIBLE);

                                }
                                if(root.findViewById(R.id.option2).getVisibility() != View.GONE){
                                    remove2.setVisibility(View.INVISIBLE);
                                }
                                start = true;
                            }
                        }
                    }

                });
            }

        });


        remove1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.findViewById(R.id.option1).setVisibility(View.GONE);
                remove1.setVisibility(View.GONE);
                for(int i = 1; i< optionCounter; i++){
                    if(map.containsKey(i+1)) {
                        map.get(i + 1).setHint("Option " + (i));
                        map.replace(i, map.get(i + 1));
                        remove.replace(i, remove.get(i + 1));
                    }
                }

                optionCounter--;
            }
        });


        remove2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                root.findViewById(R.id.option2).setVisibility(View.GONE);
                remove2.setVisibility(View.GONE);

                for(int i = 1; i< optionCounter; i++){
                    if(map.containsKey(i+1)) {
                        map.get(i + 1).setHint("Option " + (i));
                        map.replace(i, map.get(i + 1));
                        remove.replace(i, remove.get(i + 1));
                    }
                }

                optionCounter--;
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