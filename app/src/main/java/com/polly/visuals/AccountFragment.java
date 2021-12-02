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

public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    TextView emailInfo;
    TextView fullnameInfo;
    TextView usernameInfo;
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        emailInfo = (TextView) view.findViewById(R.id.email_text);
        fullnameInfo = (TextView) view.findViewById(R.id.fullname_text);
        usernameInfo = (TextView) view.findViewById(R.id.username_text);
        mAuth = FirebaseAuth.getInstance();
        Button logout = (Button) view.findViewById(R.id.logout_button);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mAuth.getCurrentUser() != null) {
                    FirebaseAuth.getInstance().signOut();
                    Toast.makeText(getActivity(), "You are now signed out", Toast.LENGTH_SHORT).show();
                    emailInfo.setText("");
                    fullnameInfo.setText("");
                    usernameInfo.setText("");
                    LoginManager.getInstance().logOut();
                    GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestIdToken(getString(R.string.default_web_client_id)).requestEmail().build();
                    GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(getActivity(), googleSignInOptions);
                    googleSignInClient.signOut();
                }
            }
        });

        checkUser();


        return view;
    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(getActivity(), "You are currently not signed in", Toast.LENGTH_SHORT).show();
        }
        else{
            FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid()).child("fullname").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String fullname = snapshot.getValue(String.class);
                    fullnameInfo.setText(fullname);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                   Toast.makeText(getContext(), "Something went wrong, I can feel it", Toast.LENGTH_SHORT);
                }
            });

            FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid()).child("email").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String email = snapshot.getValue(String.class);
                    emailInfo.setText(email);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Something went wrong, I can feel it", Toast.LENGTH_SHORT);
                }
            });

            FirebaseDatabase.getInstance("https://polly-abdd4-default-rtdb.europe-west1.firebasedatabase.app/").getReference("Users").child(firebaseUser.getUid()).child("username").addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    String username = snapshot.getValue(String.class);
                    usernameInfo.setText(username);
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    Toast.makeText(getContext(), "Something went wrong, I can feel it", Toast.LENGTH_SHORT);
                }
            });

        }
    }

}
