package com.polly.visuals;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.polly.R;
import com.polly.testclasses.DBHelper;

import org.w3c.dom.Text;

public class SignupFragment extends Fragment {
    /**private boolean password_is_good = false;
    public DBHelper myDB;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        myDB = new DBHelper(getContext());
        View view = inflater.inflate(R.layout.fragment_sign_up, container, false);
        Button signup = (Button) view.findViewById(R.id.activity_sign_up_button_sign_up);
        EditText password = (EditText) view.findViewById(R.id.activity_sign_up_edittext_password);
        EditText confirmpassword = (EditText) view.findViewById(R.id.activity_sign_up_edittext_password_confirm);
        CheckBox viewPswd = (CheckBox) view.findViewById(R.id.shw_psswords);
        viewPswd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    confirmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                } else {
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    confirmpassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
            }
        });
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
                    if(myDB.checkUsername(username.getText().toString()))
                        Toast.makeText(getActivity(), "The Username you entered already exists", Toast.LENGTH_SHORT).show();
                    else {
                       if(myDB.insertData(username.getText().toString(), password.getText().toString())) {
                           Toast.makeText(getActivity(), "Registration was successful", Toast.LENGTH_SHORT).show();
                           accountViewModel viewMod = new ViewModelProvider(requireActivity()).get(accountViewModel.class);
                           viewMod.setUsername(username.getText().toString());
                           viewMod.setEmail(email.getText().toString());
                           String user = viewMod.getUsername();
                       }
                       else
                           Toast.makeText(getActivity(), "Registration failed", Toast.LENGTH_SHORT).show();
                    }

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


**/
};


