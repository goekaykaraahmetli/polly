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
import com.polly.utils.EnterPoll;


public class EnterViaCodeFragment extends Fragment {
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                         @Nullable ViewGroup container,
                         @Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.enter_via_code, container, false);
        Button enterPollBtn = (Button) root.findViewById(R.id.enterpollbtn);
        EditText codeInput = (EditText) root.findViewById(R.id.polly_code);


        enterPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable codeEditable = codeInput.getText();
                CharSequence code = codeEditable.toString();
                if(code.length() == 0 || code.length() > 6)
                    Toast.makeText(getActivity(), "The entered code has the wrong size. Try again", Toast.LENGTH_LONG).show();
                else {
                    Toast.makeText(getActivity(), "Code has the right format. Your code is: " + code, Toast.LENGTH_SHORT).show();

                    long id = Long.valueOf((String) code);
                    EnterPoll.enterPoll(getContext(), id);

                }
            }
        });
        return root;
    }
}
