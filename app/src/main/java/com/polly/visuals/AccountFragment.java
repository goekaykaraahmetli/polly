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
    private FirebaseUser user;
    private DatabaseReference reference;
    private String userID;
    @Nullable
    public View onCreateView(LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_account, container, false);
        TextView emailInfo = (TextView) view.findViewById(R.id.email_info);
        TextView fullNameInfo = (TextView) view.findViewById(R.id.fullname_text);
        TextView usernameInfo = (TextView) view.findViewById(R.id.username_info);

        Button logout = (Button) view.findViewById(R.id.logout_button);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Toast.makeText(getActivity(), "You are now signed out", Toast.LENGTH_LONG).show();
                emailInfo.setText("Email:");
                fullNameInfo.setText("Full Name:");
                usernameInfo.setText("Username:");
            }
        });

        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        if(user != null){
            userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if(userProfile != null){
                    String fullName = userProfile.fullname;
                    String email = userProfile.email;
                    String username = userProfile.username;
                    emailInfo.setText("Email: \n" + email);
                    fullNameInfo.setText("Full Name: \n" + fullName);
                    usernameInfo.setText("Username: \n" + username);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_LONG).show();
            }
        });}
        return view;
    }

}
