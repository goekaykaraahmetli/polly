package com.polly.visuals;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.polly.R;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.polly.testclasses.User;

import java.util.zip.Inflater;

public class GoogleLogin extends AppCompatActivity {
    FirebaseAuth mAuth;
    EditText editTextFullname;
    EditText editTextPasswordConf;
    EditText editTextPassword;
    EditText editTextUsername;
    EditText editTextEmail;
    CheckBox checkbox;
    Boolean password_is_good = false;
    private static final int RC_SIGN_IN = 9001;
    private GoogleSignInClient googleSignInClient;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_sign_up);

        mAuth = FirebaseAuth.getInstance();
        editTextEmail = (EditText) findViewById(R.id.activity_sign_up_edittext_email);
        editTextUsername = (EditText) findViewById(R.id.activity_sign_up_edittext_username);
        editTextPassword = (EditText) findViewById(R.id.activity_sign_up_edittext_password);
        editTextPasswordConf = (EditText) findViewById(R.id.activity_sign_up_edittext_password_confirm);
        editTextFullname = (EditText) findViewById(R.id.activity_sign_up_edittext_fullname);
        checkbox = (CheckBox) findViewById(R.id.activity_sign_up_checkbox_accept_terms_of_service);

        /**
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        ImageButton googleBtn = (ImageButton) findViewById(R.id.googlesigninbtn);
        googleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = googleSignInClient.getSignInIntent();
                startActivityForResult(intent, RC_SIGN_IN);
            }
        });
        **/

        CheckBox viewPswd = (CheckBox) findViewById(R.id.shw_psswords);
        viewPswd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                editTextPassword.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                editTextPasswordConf.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
            }
        });

        editTextPassword.addTextChangedListener(textWatcher);
        Button signup = (Button) findViewById(R.id.activity_sign_up_button_sign_up);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

    }
    /**
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN) {
            Log.d("TAG", "onActivityResult: Google Signin intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch (Exception e){
                Log.d("TAG", "onActivityResult: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account) {
        Log.d("TAG", "firebaseAuthWithGoogleAccount: begin firebase auth with google account");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d("TAG", "onSuccess: Logging In");
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                Log.d("TAG", "onSuccess: Email: " + email);
                Log.d("TAG", "onSuccess: UID: " + uid);

                if(authResult.getAdditionalUserInfo().isNewUser()){
                    Log.d("TAG", "onSuccess: Account Created: " +email);
                    Toast.makeText(GoogleLogin.this, "Account Created", Toast.LENGTH_LONG).show();
                }
                else{
                    Log.d("TAG", "onSuccess: Existing user: " + email);
                    Toast.makeText(GoogleLogin.this, "Existing user: " +email, Toast.LENGTH_LONG).show();
                }
            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("TAG", "onFailure: Login failed " + e.getMessage());

            }
        });
    } **/

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
                    FirebaseDatabase.getInstance().getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user).addOnCompleteListener(task1 -> {
                        if (task1.isSuccessful()) {
                            Toast.makeText(GoogleLogin.this, "User has been registered successfully!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(GoogleLogin.this, "Failed to register. Try again", Toast.LENGTH_LONG).show();
                        }
                    });
                } else
                    Toast.makeText(GoogleLogin.this, "Failed to register. Try again", Toast.LENGTH_LONG).show();
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

            TextView strength = ((TextView) findViewById(R.id.password_strength));
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



