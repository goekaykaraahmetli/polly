package com.polly;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class FirstFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_first, container, false);

        TextView b = (TextView) view.findViewById(R.id.button10);
        b.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_firstFragment_to_secondFragment));

        return view;
    }
}