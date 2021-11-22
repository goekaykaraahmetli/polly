package com.polly.visuals;


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
import android.widget.ScrollView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.polly.R;

public class CreatePollFragment extends Fragment {
    int optionCounter = 3;
    float yheight = 0;

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
                }else{
                    Toast.makeText(getActivity(), "Poll '" + poll + "' created", Toast.LENGTH_SHORT).show();
                    testViewModel viewModel = new ViewModelProvider(requireActivity()).get(testViewModel.class);
                    viewModel.setNewPoll(poll.toString());
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



        Button addOption = (Button) root.findViewById(R.id.addOption);
        EditText option = (EditText) root.findViewById(R.id.option2);
        addOption.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (optionCounter <= 8) {
                    Toast.makeText(getActivity(), "Added new Option", Toast.LENGTH_SHORT).show();
                    EditText newOption = new EditText(getContext());
                    newOption.setHint("Option " + optionCounter);
                    int width = option.getLayoutParams().width;
                    int height = option.getLayoutParams().height;
                    ConstraintLayout.LayoutParams params = new ConstraintLayout.LayoutParams(width, height);
                    newOption.setLayoutParams(params);
                    newOption.setX(option.getX());
                    if (optionCounter == 3) {
                        newOption.setY(option.getY() + 150);
                    } else {
                        newOption.setY(yheight);
                    }

                    newOption.setGravity(1);
                    ((ConstraintLayout) root.findViewById(R.id.createPoll_constraintLayout)).addView(newOption);
                    addOption.setY(addOption.getY() + 150);
                    optionCounter++;

                    Button delete = new Button(getContext());
                    delete.setText("remove option");
                    delete.setY(newOption.getY());
                    delete.setX(newOption.getX()+600);
                    ConstraintLayout.LayoutParams params2 = new ConstraintLayout.LayoutParams(width-150, height);
                    delete.setLayoutParams(params2);
                    ((ConstraintLayout) root.findViewById(R.id.createPoll_constraintLayout)).addView(delete);
                    yheight = newOption.getY()+150;

                    delete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            newOption.setVisibility(View.GONE);
                            delete.setVisibility(View.GONE);
                            addOption.setY(addOption.getY()-150);
                            optionCounter--;
                            yheight -= 150;
                            addOption.setVisibility(View.VISIBLE);
                        }
                    });
                    if(optionCounter == 8){
                        addOption.setVisibility((View.GONE));
                    }
                }else{
                    addOption.setVisibility(View.GONE);
                    Toast.makeText(getActivity(), "Can't add any more options", Toast.LENGTH_SHORT).show();
                }
            }
        });
        return root;
    }
}