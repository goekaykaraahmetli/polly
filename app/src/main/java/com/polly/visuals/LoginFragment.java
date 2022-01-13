package com.polly.visuals;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import androidx.navigation.Navigation;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
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
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;
import com.polly.R;
import com.polly.config.Config;
import com.polly.utils.Organizer;
import com.polly.utils.command.user.IsUsernameAvailableCommand;
import com.polly.utils.command.user.LoginCommand;
import com.polly.utils.command.user.RegisterCommand;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.LoginAnswerWrapper;
import com.polly.utils.wrapper.Message;

import java.io.IOException;

public class LoginFragment extends Fragment {
    private static ResponseCommunicator communicator = initialiseCommunicator();
    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("LoginFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

                for(Long l : communicator.responseIds){
                    System.out.println(l);
                }

                // no default input handling
            }
        };
    }
    EditText passwordInput;
    EditText emailInput;
    FirebaseAuth mAuth;
    CallbackManager mCallbackManager;
    private static final int RC_SIGN_IN = 100;
    private GoogleSignInClient googleSignInClient;
    private static final String TAG = "GOOGLE_SIGN_IN_TAG";
    View view;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_login, container, false);
        Button b = (Button) view.findViewById(R.id.activity_login_button_sign_up);
        b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.signupFragment);
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
            Log.d(TAG, "onClick: begin Google SignIn");
            Intent intent = googleSignInClient.getSignInIntent();
            startActivityForResult(intent, RC_SIGN_IN);
        });

        mCallbackManager = CallbackManager.Factory.create();

        LoginButton loginButton = (LoginButton) view.findViewById(R.id.login_button);
        loginButton.setFragment(this);
        loginButton.setReadPermissions("email", "public_profile");
        loginButton.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.d(TAG, "facebook:onSuccess:" + loginResult);
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {
                Log.d(TAG, "facebook:onCancel");
            }

            @Override
            public void onError(@NonNull FacebookException e) {
                Log.d(TAG, "facebook:onError", e);
            }
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
                    sendTokenToServer(false);
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

    public static void sendTokenToServer(boolean starting){
        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        mUser.getIdToken(true)
                .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                    public void onComplete(@NonNull Task<GetTokenResult> task) {
                        if (task.isSuccessful()) {
                            String idToken = task.getResult().getToken();
                            LoginCommand loginCommand = new LoginCommand(idToken);
                            try {
                                Message messageResponse = communicator.sendWithResponse(Config.serverCommunicationId, loginCommand);
                                if(messageResponse.getDataType() == LoginAnswerWrapper.class) {
                                    LoginAnswerWrapper loginAnswerWrapper = ((LoginAnswerWrapper) messageResponse.getData());
                                    if(!loginAnswerWrapper.isSuccessful()) {
                                        if (loginAnswerWrapper.getMessage().equals("User does not exist"))
                                            chooseUsernameAlert(idToken);
                                        else
                                            Toast.makeText(MainActivity.mainActivity, "Login failed: " + loginAnswerWrapper.getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                    else if(!starting){
                                        Toast.makeText(MainActivity.mainActivity, "Logged in successfully", Toast.LENGTH_SHORT).show();
                                        Navigation.findNavController(MainActivity.mainActivity, R.id.nav_host_fragment).navigate(R.id.accountFragment);
                                    }
                                    else{

                                    }
                                } else if(messageResponse.getDataType() == ErrorWrapper.class)
                                    Toast.makeText(MainActivity.mainActivity, ((ErrorWrapper) messageResponse.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                                else
                                    Toast.makeText(MainActivity.mainActivity, "Something went wrong", Toast.LENGTH_SHORT).show();
                            } catch (IOException e) {
                                System.out.println("HIER FEHLER ------------------");
                                e.printStackTrace();
                            }
                        } else {
                            Log.d(TAG, task.getException().getMessage());
                        }
                    }
                });
    }

    private static void chooseUsernameAlert(String idToken) {
        AlertDialog.Builder alert = new AlertDialog.Builder(MainActivity.mainActivity);
        alert.setTitle("Select Username");
        alert.setMessage("You are new here, please enter an username");
        EditText usernameInput = new EditText(MainActivity.mainActivity);
        alert.setView(usernameInput);
        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (usernameInput.getText().toString() == "")
                    Toast.makeText(MainActivity.mainActivity, "Please enter at least one character", Toast.LENGTH_SHORT).show();
                else {
                    IsUsernameAvailableCommand com = new IsUsernameAvailableCommand(usernameInput.getText().toString());
                    try {
                        Message booleanMessage = communicator.sendWithResponse(Config.serverCommunicationId, com);
                        if (booleanMessage.getDataType() == Boolean.class) {
                            boolean isFree = (Boolean) booleanMessage.getData();
                            if (isFree) {
                                Message message = communicator.sendWithResponse(Config.serverCommunicationId, new RegisterCommand(idToken, usernameInput.getText().toString()));
                                if (message.getDataType() == LoginAnswerWrapper.class) {
                                    LoginAnswerWrapper answer = (LoginAnswerWrapper) message.getData();

                                    if (answer.isSuccessful())
                                        Navigation.findNavController(MainActivity.mainActivity, R.id.nav_host_fragment).navigate(R.id.accountFragment);
                                    else
                                        Toast.makeText(MainActivity.mainActivity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                } else if (message.getDataType() == ErrorWrapper.class) {
                                    Toast.makeText(MainActivity.mainActivity, ((ErrorWrapper) message.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(MainActivity.mainActivity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                                }
                            } else {
                                Toast.makeText(MainActivity.mainActivity, "This username already exists", Toast.LENGTH_SHORT).show();
                                chooseUsernameAlert(idToken);
                            }
                        } else if (booleanMessage.getDataType() == ErrorWrapper.class) {
                            Toast.makeText(MainActivity.mainActivity, ((ErrorWrapper) booleanMessage.getData()).getMessage(), Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(MainActivity.mainActivity, "Something went wrong, please try again", Toast.LENGTH_SHORT).show();
                        }


                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        alert.show();
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
                Log.d(TAG, "Google Sign In Failed " + e.getMessage());
            }
        }
        else
            mCallbackManager.onActivityResult(requestCode, resultCode, data);
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
                /**
                if(authResult.getAdditionalUserInfo().isNewUser()){
                    Log.d(TAG, "onSuccess: Account Created");
                    Toast.makeText(getActivity(), "Account Created:\n" + email, Toast.LENGTH_SHORT).show();
                }
                else{
                    Log.d(TAG, "onSuccess: Existing User:\n" + email);
                    Toast.makeText(getActivity(), "Existing User:\n" + email, Toast.LENGTH_SHORT);
                }
                FirebaseUser user = mAuth.getCurrentUser();
                FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if(!snapshot.hasChild(user.getUid())){
                            AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                            alert.setTitle("Select Username");
                            alert.setMessage("Please enter an username");
                            EditText usernameInput = new EditText(getContext());
                            alert.setView(usernameInput);
                            alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    User user_polly = new User(user.getDisplayName(), user.getEmail(), usernameInput.getText().toString());
                                    FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user_polly).addOnCompleteListener(task1 -> {
                                        if (task1.isSuccessful()) {
                                            Toast.makeText(getActivity(), "You are now signed in", Toast.LENGTH_SHORT).show();
                                        } else {
                                            Toast.makeText(getActivity(), "Failed to sign in. Try again", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }

                            });
                            alert.show();
                        }
                        else {
                            Toast.makeText(getActivity(), "You are now signed in", Toast.LENGTH_SHORT).show();
                            sendTokenToServer();
                        }
                        Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.accountFragment);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(getContext(), "Something went wrong, I can feel it", Toast.LENGTH_SHORT).show();
                    }
                });

            **/
                sendTokenToServer(false);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "onFailure: Log In failed " + e.getMessage());
            }
        });
    }
    private void handleFacebookAccessToken(AccessToken token) {
        Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(getActivity(), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            sendTokenToServer(false);
                            /**
                            FirebaseUser user = mAuth.getCurrentUser();
                            FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if(!snapshot.hasChild(user.getUid())){
                                        AlertDialog.Builder alert = new AlertDialog.Builder(getContext());
                                        alert.setTitle("Select Username");
                                        alert.setMessage("Please enter an username");
                                        EditText usernameInput = new EditText(getContext());
                                        alert.setView(usernameInput);
                                        alert.setPositiveButton("Enter", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                User user_polly = new User(user.getDisplayName(), user.getEmail(), usernameInput.getText().toString());
                                                FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).setValue(user_polly).addOnCompleteListener(task1 -> {
                                                    if (task1.isSuccessful()) {
                                                        Toast.makeText(getActivity(), "You are now signed in", Toast.LENGTH_SHORT).show();
                                                    } else {
                                                        Toast.makeText(getActivity(), "Failed to sign in. Try again", Toast.LENGTH_SHORT).show();
                                                    }
                                                });
                                            }

                                        });
                                        alert.show();
                                    }
                                    else {
                                        Toast.makeText(getActivity(), "You are now signed in", Toast.LENGTH_SHORT).show();
                                        sendTokenToServer();
                                    }

                                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.accountFragment);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                    Toast.makeText(getContext(), "Something went wrong, I can feel it", Toast.LENGTH_SHORT).show();
                                }
                            });**/

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getContext(), "Auth Failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
