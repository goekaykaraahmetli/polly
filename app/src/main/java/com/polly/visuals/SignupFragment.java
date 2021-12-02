package com.polly.visuals;

import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.LinkMovementMethod;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.polly.R;
import com.polly.testclasses.User;

public class SignupFragment extends Fragment {
    FirebaseAuth mAuth;
    EditText editTextFullname;
    EditText editTextPasswordConf;
    EditText editTextPassword;
    EditText editTextUsername;
    EditText editTextEmail;
    CheckBox checkbox;
    Boolean password_is_good = false;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) root.findViewById(R.id.activity_sign_up_edittext_email);
        editTextUsername = (EditText) root.findViewById(R.id.activity_sign_up_edittext_username);
        editTextPassword = (EditText) root.findViewById(R.id.activity_sign_up_edittext_password);
        editTextPasswordConf = (EditText) root.findViewById(R.id.activity_sign_up_edittext_password_confirm);
        editTextFullname = (EditText) root.findViewById(R.id.activity_sign_up_edittext_fullname);
        checkbox = (CheckBox) root.findViewById(R.id.activity_sign_up_checkbox_accept_terms_of_service);
        TextView terms = (TextView) root.findViewById(R.id.acceptTerms);
        terms.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.termsOfService);
            }
        });


        CheckBox viewPswd = (CheckBox) root.findViewById(R.id.shw_psswords);
        viewPswd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editTextPasswordConf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        editTextPassword.addTextChangedListener(textWatcher);
        Button signup = (Button) root.findViewById(R.id.activity_sign_up_button_sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        return root;
    }


    private void registerUser() {
        String email = editTextEmail.getText().toString();
        String password = editTextPassword.getText().toString();
        String fullname = editTextFullname.getText().toString();
        String confirmPass = editTextPasswordConf.getText().toString();
        String username = editTextUsername.getText().toString();

        if (username.equals("")) {
            editTextUsername.setError("The username field is Empty");
            editTextUsername.requestFocus();
        } else if (password.equals("")) {
            editTextPassword.setError("The password field is Empty");
            editTextPassword.requestFocus();
        } else if (!password.matches(confirmPass)) {
            editTextPassword.setError("Your passwords doesn't match");
            editTextPassword.requestFocus();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            editTextEmail.setError("Your E-Mail is not valid");
            editTextEmail.requestFocus();
        } else if (!checkbox.isChecked()) {
            checkbox.setError("Please accept the terms of service");
            checkbox.requestFocus();
        } else if (!password_is_good) {
            editTextPassword.setError("Your Password is too weak");
            editTextPassword.requestFocus();
        } else {
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    User user = new User(fullname, email, username);
                    FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(getContext(), "User has been registered successfully! Please verify your email before you sign in.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getContext(), "Failed to register. Try again", Toast.LENGTH_SHORT).show();
                        }
                    });
                    mAuth.getCurrentUser().sendEmailVerification();
                    mAuth.signOut();


                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.loginFragment);
                } else
                    Toast.makeText(getContext(), "Failed to register. Try again", Toast.LENGTH_SHORT).show();
            });
        }

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
            if (s.length() < 6) {
                strength.setText("Not even close \n (Your password needs at least 6 digits, a uppercase letter, a number and a special character)");
                strength.setTextColor(Color.GRAY);
                password_is_good = false;
            } else if (s.length() >= 6 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && !s.chars().anyMatch(x -> Character.isDigit(x)) && !s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                strength.setText("A little stronger");
                strength.setTextColor(Color.RED);
                password_is_good = false;
            } else if (s.length() >= 6 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && s.chars().anyMatch(x -> Character.isDigit(x)) && !s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                strength.setText("We're getting there");
                strength.setTextColor(getResources().getColor((R.color.orange)));
                password_is_good = false;
            } else if (s.length() >= 6 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && s.chars().anyMatch(x -> Character.isDigit(x)) && s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                strength.setText("You'r doing great!");
                strength.setTextColor(Color.GREEN);
                password_is_good = true;
            } else if (s.length() >= 15 && s.chars().anyMatch(x -> Character.isUpperCase(x)) && s.chars().anyMatch(x -> Character.isDigit(x)) && s.chars().anyMatch(x -> isSpecialSymbol(x))) {
                strength.setText("It's over 9000!!!");
                strength.setTextColor(Color.MAGENTA);
            } else if (s.length() >= 20) {
                strength.setText("That's a overkill");
                strength.setTextColor(Color.CYAN);

            }
        }
    };

    private boolean isSpecialSymbol(int c) {
        if ((33 <= c && c <= 47) || (58 <= c && c <= 64) || (91 <= c && c <= 96) || (123 <= c && c <= 126))
            return true;
        return false;

    }
}



