package com.polly.visuals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.polly.R;

public class AccountFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        TextView emailInfo = (TextView) view.findViewById(R.id.email_info);
        SignupFragment signupFragment = (SignupFragment) getFragmentManager().findFragmentById(signup);
        emailInfo.setText("Email: \n" + signupFragment.email_global );
        return view;
    }

}
