package com.polly.visuals;

import android.app.Activity;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.polly.R;
import com.polly.testclasses.DBHelper;

public class LoginFragment extends Fragment {
    EditText passwordInput;
    EditText emailInput;
    FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
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


        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(getActivity(),googleSignInOptions);
        ImageButton googleBtn = (ImageButton) view.findViewById(R.id.imageButtonGoogle);
        googleBtn.setOnClickListener(v -> {
            Toast.makeText(getActivity(), "This Button Still works", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "onClick: begin Google SignIn");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });





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
                    Toast.makeText(getActivity(), "You are now logged in!", Toast.LENGTH_SHORT).show();

                }
                else{
                    user.sendEmailVerification();
                    Toast.makeText(getActivity(), "Check your Email to verify your account", Toast.LENGTH_SHORT).show();
                }
            }
            else
                Toast.makeText(getActivity(), "Failed to login! Check your credentials", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == RC_SIGN_IN){
            Log.d(TAG, "onActivityResult: Google SignIn intent result");
            Task<GoogleSignInAccount> accountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = accountTask.getResult(ApiException.class);
                firebaseAuthWithGoogleAccount(account);
            }
            catch(Exception e){
                Log.d(TAG, "onActivityResult: " + e.getMessage());
            }
        }
    }

    private void firebaseAuthWithGoogleAccount(GoogleSignInAccount account){
        Log.d(TAG, "fireBaseAuthWithGoogleAccount: begin firebase auth with google acc");
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Log.d(TAG, "onSuccess: Logged In");
                FirebaseUser firebaseUser = mAuth.getCurrentUser();
                String uid = firebaseUser.getUid();
                String email = firebaseUser.getEmail();
                Log.d(TAG, "onSuccess: Email: "+email);
                Log.d(TAG, "onSuccess: UID: "+uid);

                if(authResult.getAdditionalUserInfo().isNewUser()){
                    Log.d(TAG, "onSuccess: Account Created");
                    Toast.makeText(getActivity(), "Account Created:\n" + email, Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "onSuccess: Existing User:\n" + email);
                    Toast.makeText(getActivity(), "Existing User:\n" + email, Toast.LENGTH_SHORT);
                }

                AccountFragment accountFragment = new AccountFragment();
                FragmentTransaction transaction = getFragmentManager().beginTransaction();
                transaction.replace(R.id.fragment_container, accountFragment);
                transaction.addToBackStack(null);
                transaction.commit();

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Log In failed "+e.getMessage());
            }
        });
    }
}
