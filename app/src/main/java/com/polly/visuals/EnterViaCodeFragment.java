package com.polly.visuals;

import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.polly.R;
import com.polly.utils.ShowPollPage;
import com.polly.utils.exceptions.CanNotEnterPollException;

import java.io.IOException;


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
        codeInput.setInputType(InputType.TYPE_CLASS_NUMBER);

        enterPollBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Editable codeEditable = codeInput.getText();
                CharSequence code = codeEditable.toString();
                if(code.length() == 0 || code.length() > 6)
                    Toast.makeText(getActivity(), "The entered code has the wrong size. Try again", Toast.LENGTH_SHORT).show();
                else {
                    //Toast.makeText(getActivity(), "Code has the right format. Your code is: " + code, Toast.LENGTH_SHORT).show();

                    long id = Long.valueOf((String) code);

                    try {
                        ShowPollPage.showPollVotingPage(id);
                    } catch (IllegalStateException | IllegalArgumentException | IOException e){
                        Toast.makeText(getContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                        e.printStackTrace();
                    }
                }
            }
        });
        return root;
    }
}
