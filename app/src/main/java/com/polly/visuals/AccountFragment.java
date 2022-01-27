package com.polly.visuals;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.config.Config;
import com.polly.utils.command.user.GetMyUsergroupsCommand;
import com.polly.utils.command.user.GetUsernameCommand;
import com.polly.utils.communicator.Communicator;
import com.polly.utils.communicator.ResponseCommunicator;
import com.polly.utils.user.UserManager;
import com.polly.utils.wrapper.ErrorWrapper;
import com.polly.utils.wrapper.Message;

import java.io.IOException;

public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    TextView emailInfo;
    TextView fullnameInfo;
    TextView usernameInfo;

    private static ResponseCommunicator communicator = initialiseCommunicator();
    private static ResponseCommunicator initialiseCommunicator(){
        return new ResponseCommunicator() {
            @Override
            public void handleInput(Message message) {
                System.out.println("AccountFragment received message from " + message.getSender() + " with responseId " + message.getResponseId());
                System.out.println("from type: " + message.getDataType().getName());

                for(Long l : communicator.responseIds){
                    System.out.println(l);
                }

                // no default input handling
            }
        };
    }

    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        if(FirebaseAuth.getInstance().getCurrentUser() == null){
            Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.loginFragment);
        }
        emailInfo = (TextView) view.findViewById(R.id.email_text);
        usernameInfo = (TextView) view.findViewById(R.id.username_text);
        mAuth = FirebaseAuth.getInstance();
        Button logout = (Button) view.findViewById(R.id.logout_button);
        if(mAuth.getCurrentUser() == null){
            view.findViewById(R.id.logout_button).setVisibility(View.INVISIBLE);
        }else{
            view.findViewById(R.id.logout_button).setVisibility(View.VISIBLE);
        }


        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getActivity(), "You are now signed out", Toast.LENGTH_SHORT).show();
                    LoginManager.getInstance().logOut();
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
                    googleSignInClient.signOut();
                    Navigation.findNavController(getActivity(), R.id.nav_host_fragment).navigate(R.id.loginFragment);

                }
            }
        });

        checkUser();


        return view;
    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(getActivity(), "Please sign in first", Toast.LENGTH_SHORT).show();
        }
        else{
                    String email = FirebaseAuth.getInstance().getCurrentUser().getEmail();
                    emailInfo.setText(email);
                    try {
                        usernameInfo.setText(UserManager.getMyUsername());
                    }
                    catch (IOException e){
                        usernameInfo.setText("Could not find username");
                    }

        }
    }

}
