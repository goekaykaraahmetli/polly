package com.polly.visuals;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.polly.R;
import com.polly.testclasses.DBHelper;

public class LoginFragment extends Fragment {
    EditText passwordInput;
    EditText emailInput;
    FirebaseAuth mAuth;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login, container, false);
        Button b = (Button) view.findViewById(R.id.activity_login_button_sign_up);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getActivity(), GoogleLogin.class);
                startActivity(i);
                ((Activity) getActivity()).overridePendingTransition(0, 0);
            }
        });
        Button signInBtn = (Button) view.findViewById(R.id.activity_login_button_login);
        emailInput = (EditText) view.findViewById(R.id.activity_login_edittext_username);
        passwordInput = (EditText) view.findViewById(R.id.activity_login_edittext_password);
        mAuth = FirebaseAuth.getInstance();


        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userLogin();
            }
        });
        return view;
    }

    private void userLogin(){
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        if(email.isEmpty()){
            emailInput.setError("Email is required");
            emailInput.requestFocus();
            return;
        }
        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            emailInput.setError("Your Email is invalid");
            emailInput.requestFocus();
            return;
        }
        if(password.isEmpty()){
            passwordInput.setError("Password is required");
            passwordInput.requestFocus();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
            if(task.isSuccessful()){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                if(user.isEmailVerified()) {
                    Toast.makeText(getActivity(), "You are now logged in!", Toast.LENGTH_LONG).show();

                }
                else{
                    user.sendEmailVerification();
                    Toast.makeText(getActivity(), "Check your Email to verify your account", Toast.LENGTH_LONG).show();
                }
            }
            else
                Toast.makeText(getActivity(), "Failed to login! Check your credentials", Toast.LENGTH_LONG).show();
        });
    }
}
