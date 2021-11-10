package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.polly.R;

import org.w3c.dom.Text;

public class SignupFragment extends Fragment {
    private boolean password_is_good = false;
    public String username_global = "You are not";
    public String email_global = "signed in.";
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Button signup = (Button) view.findViewById(R.id.activity_sign_up_button_sign_up);
        EditText password = (EditText) view.findViewById(R.id.activity_sign_up_edittext_password);
        password.addTextChangedListener(textWatcher);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText username = (EditText) view.findViewById(R.id.activity_sign_up_edittext_username);
                EditText email = (EditText) view.findViewById(R.id.activity_sign_up_edittext_email);
                EditText password = (EditText) view.findViewById(R.id.activity_sign_up_edittext_password);
                EditText confirmpassword = (EditText) view.findViewById(R.id.activity_sign_up_edittext_password_confirm);
                CheckBox checkbox = (CheckBox) view.findViewById(R.id.activity_sign_up_checkbox_accept_terms_of_service);

                if(username.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "The username field is Empty", Toast.LENGTH_SHORT).show();
                else if(password.getText().toString().equals(""))
                    Toast.makeText(getActivity(), "The password field is Empty", Toast.LENGTH_SHORT).show();
                else if(!password.getText().toString().matches(confirmpassword.getText().toString()))
                    Toast.makeText(getActivity(), "Your passwords doesn't match", Toast.LENGTH_SHORT).show();
                else if(!Patterns.EMAIL_ADDRESS.matcher(email.getText()).matches())
                    Toast.makeText(getActivity(), "Your E-Mail is not valid", Toast.LENGTH_SHORT).show();
                else if(!checkbox.isChecked())
                    Toast.makeText(getActivity(), "Please accept the terms of service", Toast.LENGTH_SHORT).show();
                else if(!password_is_good)
                    Toast.makeText(getActivity(), "Your password is to weak", Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(getActivity(), "Thank you for signing up", Toast.LENGTH_SHORT).show();
                    username_global = username.getText().toString();
                    email_global = email.getText().toString();
                }
            }
        });

        return view;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {

                TextView strength = ((TextView) getView().findViewById(R.id.password_strength));
                if(s.length()<6) {
                    strength.setText("Not even close \n (Your password needs at least 6 digits, a uppercase letter, a number and a special character)");
                    strength.setTextColor(Color.GRAY);
                    password_is_good = false;
                }
                else if(s.length()>= 6 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && !s.chars().anyMatch(x -> Character.isDigit(x)) && !s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                    strength.setText("A little stronger");
                    strength.setTextColor(Color.RED);
                    password_is_good = false;
                }
                else if(s.length() >= 6 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && s.chars().anyMatch(x -> Character.isDigit(x)) && !s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                    strength.setText("We're getting there");
                    strength.setTextColor(getResources().getColor((R.color.orange)));
                    password_is_good = false;
                }
                else if(s.length()>= 6 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && s.chars().anyMatch(x -> Character.isDigit(x)) && s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                    strength.setText("You'r doing great!");
                    strength.setTextColor(Color.GREEN);
                    password_is_good = true;
                }
                else if (s.length() >= 15 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && s.chars().anyMatch(x -> Character.isDigit(x)) && s.chars().anyMatch(x -> isSpecialSymbol(x))){
                    strength.setText("It's over 9000!!!");
                    strength.setTextColor(Color.MAGENTA);
                }

                else if(s.length()>=20) {
                    strength.setText("That's a overkill");
                    strength.setTextColor(Color.CYAN);

                }
        }
    };

    private boolean isSpecialSymbol(int c) {
        if((33 <= c && c <= 47) || (58 <= c && c <= 64) || (91 <= c && c <= 96) || (123 <= c && c <= 126))
            return true;
        return false;

    }

};


