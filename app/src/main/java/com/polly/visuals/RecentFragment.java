package com.polly.visuals;

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

import com.polly.R;

public class RecentFragment extends Fragment {
    private LinearLayout linearLayout;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_recent, container, false);
        ScrollView scroll = (ScrollView) root.findViewById(R.id.scrollviewrecent);
        if(linearLayout ==null) {
            linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            scroll.addView(linearLayout);
        }
        testViewModel viewModel = new ViewModelProvider(requireActivity()).get(testViewModel.class);
        LiveData<String> test = viewModel.getPoll();
        String pollName = test.getValue();
        Button button = (Button) new Button(getContext());
        button.setText(pollName);
        linearLayout.addView(button);


        return root;
    }


}