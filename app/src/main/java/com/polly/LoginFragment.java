package com.polly;

import android.os.Bundle;
import android.text.Editable;
import android.text.method.KeyListener;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.polly.R;

public class LoginFragment extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);

        Button b = (Button) view.findViewById(R.id.activity_login_button_sign_up);
        b.setOnClickListener(v -> Navigation.findNavController(view).navigate(R.id.action_loginFragment_to_signupFragment));


        EditText usernameInput = (EditText) view.findViewById(R.id.activity_login_edittext_username);
        EditText passwordInput = (EditText) view.findViewById(R.id.activity_login_edittext_password);

        View.OnKeyListener validUsernameAndPasswordListener = new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(isValidUsernameInput(usernameInput.getText().toString()) && isValidPasswordInput(passwordInput.getText().toString())){
                    //TODO
                    System.out.println(usernameInput.getText().toString());
                    System.out.println(passwordInput.getText().toString());
                }
                return false;
            }
        };
        usernameInput.setOnKeyListener(validUsernameAndPasswordListener);
        passwordInput.setOnKeyListener(validUsernameAndPasswordListener);
        return view;
    }

    private boolean isValidUsernameInput(String input){
        //TODO
        if(input == ""){
            return false;
        }

        return true;
    }

    private boolean isValidPasswordInput(String input){
        //TODO
        if(input == ""){
            return false;
        }

        return true;
    }
}
