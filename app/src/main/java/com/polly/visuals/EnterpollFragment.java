package com.polly.visuals;

import android.media.Image;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.polly.R;

public class EnterpollFragment extends Fragment implements View.OnClickListener{

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_enter_poll, container, false);

        ImageButton codeScannerButton = (ImageButton) root.findViewById(R.id.codeScannerButton);
        codeScannerButton.setOnClickListener(this);
        return root;
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.codeScannerButton:
                CodeScannerFragment scannerFragment = new CodeScannerFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, scannerFragment);
                transaction.addToBackStack(null);
                transaction.commit();
        }
    }
}