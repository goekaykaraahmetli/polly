package com.polly.visuals;


import android.os.Bundle;
import android.text.Editable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.polly.R;

import java.security.cert.CertPathBuilderSpi;

public class CreatePollFragment extends Fragment {
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
                if(poll1.length() > 20){
                    Toast.makeText(getActivity(), "Entered Pollname too long.", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(getActivity(), "Poll '" + poll + "' created", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return root;
    }
}