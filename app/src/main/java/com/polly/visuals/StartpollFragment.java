package com.polly.visuals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.polly.R;

public class StartpollFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_startpoll, container, false);
        //Button b1 = (Button) view.findViewById(R.id.activity_create_poll_button1);
        //Button b2 = (Button) view.findViewById(R.id.activity_create_poll_button1);
        Button b4 = (Button) view.findViewById(R.id.button4);

/**
        Button b3 = new Button(getContext());
        b3.setVisibility(View.VISIBLE);
        b3.setText("asd");
        b3.animate();

        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(500, 100);
        params.setMargins(10,10,10,10);
        b3.setLayoutParams(params);

        ((RelativeLayout) view.findViewById(R.id.startpoll_relativeLayout)).addView(b3);**/


        b4.setOnClickListener(ButtonPress);



        return view;
    }
    private final View.OnClickListener ButtonPress = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CreatePollFragment createPollFragment = new CreatePollFragment();
            FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
            transaction1.replace(R.id.fragment_container, createPollFragment);
            transaction1.addToBackStack(null);
            transaction1.commit();
        }
    };

}