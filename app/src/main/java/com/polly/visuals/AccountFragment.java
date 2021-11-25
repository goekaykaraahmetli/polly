package com.polly.visuals;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.polly.R;
import com.polly.testclasses.User;

import org.w3c.dom.Text;

public class AccountFragment extends Fragment {
    private FirebaseAuth mAuth;
    TextView emailInfo;
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        emailInfo = (TextView) view.findViewById(R.id.email_info);
        mAuth = FirebaseAuth.getInstance();
        Button logout = (Button) view.findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "You are now signed out", Toast.LENGTH_SHORT).show();
                emailInfo.setText("Email:");
            }
        });

        checkUser();


        return view;
    }

    private void checkUser() {
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if(firebaseUser == null){
            Toast.makeText(getActivity(), "You are currently not signed in", Toast.LENGTH_SHORT);
        }
        else{
            String email = firebaseUser.getEmail();
            emailInfo.setText("Email:\n"+ email);
        }
    }

}
