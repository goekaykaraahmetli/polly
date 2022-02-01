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
import androidx.navigation.Navigation;

import com.polly.R;

public class EnterpollFragment extends Fragment {


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.activity_enter_poll, container, false);


        ImageButton codeScannerButton = (ImageButton) root.findViewById(R.id.codeScannerButton);
        ImageButton enterViaCodeButton = (ImageButton) root.findViewById(R.id.enterViaCodeButton);
        ImageButton searchPoll = (ImageButton) root.findViewById(R.id.searchPoll);
        enterViaCodeButton.setOnClickListener(mListener);
        codeScannerButton.setOnClickListener(mListener);
        searchPoll.setOnClickListener(mListener);

         return root;

    }

    private final View.OnClickListener mListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()) {
                case R.id.codeScannerButton:
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.codeScannerFragment);

                    /**CodeScannerFragment scannerFragment = new CodeScannerFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, scannerFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();*/
                    break;
                case R.id.enterViaCodeButton:
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.enterViaCodeFragment);

                    /**EnterViaCodeFragment enterViaCodeFragment = new EnterViaCodeFragment();
                    FragmentTransaction transaction1 = getFragmentManager().beginTransaction();
                    transaction1.replace(R.id.fragment_container, enterViaCodeFragment);
                    transaction1.addToBackStack(null);
                    transaction1.commit();*/
                    break;
                case R.id.searchPoll:
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.pollSearch);
                    break;
            }
        }
    };


}