package com.polly.visuals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.ActivityNavigator;

import com.polly.R;
import com.polly.testclasses.ActivityHandler;

public class RecentFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        LinearLayout linearLayout = root.findViewById(R.id.scroll_layout);
        testViewModel viewModel = new ViewModelProvider(requireActivity()).get(testViewModel.class);
        LiveData<String> test = viewModel.getPoll();
        String pollName = test.getValue();
        Button button = (Button) new Button(getContext());
        button.setText(pollName);
        button.setGravity(R.id.center);
        linearLayout.addView(button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), PollActivity.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0,0);
            }
        });
        return root;
    }


}